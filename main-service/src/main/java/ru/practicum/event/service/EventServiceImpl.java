package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.StatClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.config.AppConfig;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.UpdateRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.model.QEvent.event;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestsRepository requestsRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatClient statClient;
    private final AppConfig appConfig;
    private final Validator validator;

    @Override
    @Transactional
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        User initiator = validator.validateAndGetUser(userId);
        Category category = validator.validateAndGetCategory(newEventDto.getCategory());
        validator.checkDateTime(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0L);
        }
        Event newEvent = eventMapper.toEvent(newEventDto);
        newEvent.setCategory(category);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setInitiator(initiator);
        newEvent.setPublishedOn(LocalDateTime.now());
        newEvent.setState(State.PENDING);
        newEvent.setConfirmedRequests(0L);
        eventRepository.save(newEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(newEvent);
        eventFullDto.setViews(0L);
        log.info("Событие с id: {} создано", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long userId, long eventId) {
        validator.checkUserId(userId);
        Event event = validator.validateAndGetEvent(eventId);
        List<ViewStatsDto> viewStats = getViewStats(List.of(event));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        if (!CollectionUtils.isEmpty(viewStats)) {
            eventFullDto.setViews(viewStats.getFirst().getHits());
        } else {
            eventFullDto.setViews(0L);
        }
        log.info("Получение события с id: {}", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(long userId, int from, int size) {
        validator.checkUserId(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        BooleanExpression byUserId = event.initiator.id.eq(userId);
        Page<Event> pageEvents = eventRepository.findAll(byUserId, pageRequest);
        List<Event> events = pageEvents.getContent();
        setViews(events);
        List<EventShortDto> eventsShortDto = eventMapper.toListEventShortDto(events);
        log.info("Получение списка событий пользователя с id: {}", userId);
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, long eventId, EventUserRequestDto eventUserRequestDto) {
        validator.checkUserId(userId);
        Event event = validator.validateAndGetEvent(eventId);
        validator.checkEventPublished(event);
        if (eventUserRequestDto.getAnnotation() != null && !eventUserRequestDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUserRequestDto.getAnnotation());
        }
        if (eventUserRequestDto.getCategory() != null) {
            Category category = validator.validateAndGetCategory(eventUserRequestDto.getCategory());
            event.setCategory(category);
        }
        if (eventUserRequestDto.getDescription() != null && !eventUserRequestDto.getDescription().isBlank()) {
            event.setDescription(eventUserRequestDto.getDescription());
        }
        if (eventUserRequestDto.getEventDate() != null) {
            validator.checkDateTime(eventUserRequestDto.getEventDate(), LocalDateTime.now().plusHours(2));
            event.setEventDate(eventUserRequestDto.getEventDate());
        }
        if (eventUserRequestDto.getLocation() != null) {
            event.setLocation(eventUserRequestDto.getLocation());
        }
        if (eventUserRequestDto.getPaid() != null) {
            event.setPaid(eventUserRequestDto.getPaid());
        }
        if (eventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUserRequestDto.getParticipantLimit());
        }
        if (eventUserRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUserRequestDto.getRequestModeration());
        }
        if (eventUserRequestDto.getTitle() != null && !eventUserRequestDto.getTitle().isBlank()) {
            event.setTitle(eventUserRequestDto.getTitle());
        }
        if (eventUserRequestDto.getStateAction() != null) {
            switch (eventUserRequestDto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
            }
        }
        log.info("Событие с id: {} обновлено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestByEventId(long userId, long eventId) {
        validator.checkUserId(userId);
        validator.checkEventId(eventId);
        List<Request> requests = requestsRepository.findByEventId(eventId);
        log.info("Получение списка запросов для события с id: {}", eventId);
        return requestMapper.toListRequestDto(requests);
    }

    @Override
    @Transactional
    public RequestStatusUpdateDto updateRequestByEventId(long userId, long eventId, UpdateRequestDto updateRequestDto) {
        log.info("The beginning of the process of update a requests");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> confirmedRequests = requestsRepository.findAllByStatusAndEventId(eventId, Status.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirmedRequests.size()) {
            throw new RestrictionsViolationException("The limit on applications for this event has been reached, " +
                    "there are " + (event.getParticipantLimit() - event.getConfirmedRequests()) + " free places");
        }
        List<Request> requests = requestsRepository.findByIdIn(updateRequestDto.getRequestIds());
        if (requests.stream().map(Request::getStatus).anyMatch(status -> !status.equals(Status.PENDING))) {
            throw new RestrictionsViolationException("The status can only be changed for applications that are " +
                    "in the PENDING state");
        }
        requests.forEach(request -> request.setStatus(updateRequestDto.getStatus()));
        if (updateRequestDto.getStatus().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + updateRequestDto.getRequestIds().size());
        }
        log.info("The requests was updated");
        return requestMapper.toRequestStatusUpdateDto(null, requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  boolean onlyAvailable, EventPublicSort sort, int from, int size) {
        log.info("The beginning of the process of finding a events by public");

        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new DateException("Start time after end time");
        }
        Page<Event> events;
        PageRequest pageRequest = getCustomPage(from, size, sort);
        BooleanBuilder builder = new BooleanBuilder();

        if (text != null) {
            builder.and(event.annotation.containsIgnoreCase(text.toLowerCase())
                    .or(event.description.containsIgnoreCase(text.toLowerCase())));
        }

        if (!CollectionUtils.isEmpty(categories)) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            builder.and(event.eventDate.between(LocalDateTime.MIN, rangeEnd));
        } else if (rangeStart != null) {
            builder.and(event.eventDate.between(rangeStart, LocalDateTime.MAX));
        }

        if (onlyAvailable) {
            builder.and(event.participantLimit.eq(0L))
                    .or(event.participantLimit.gt(event.confirmedRequests));
        }

        if (builder.getValue() != null) {
            events = eventRepository.findAll(builder.getValue(), pageRequest);
        } else {
            events = eventRepository.findAll(pageRequest);
        }

        setViews(events.getContent());
        log.info("The events was found by public");
        return eventMapper.toListEventShortDto(events.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(long id) {
        log.info("The beginning of the process of finding a event by public");
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        setViews(List.of(event));
        log.info("The event was found by public");
        return eventMapper.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("The beginning of the process of finding a events by admin");
        Page<Event> pageEvents;
        PageRequest pageRequest = getCustomPage(from, size, null);
        BooleanBuilder builder = new BooleanBuilder();

        if (!CollectionUtils.isEmpty(users) && !users.contains(0L)) {
            builder.and(event.initiator.id.in(users));
        }

        if (state != null) {
            builder.and(event.state.eq(state));
        }

        if (!CollectionUtils.isEmpty(categories) && !categories.contains(0L)) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new DateException("Start time after end time");
            }
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            builder.and(event.eventDate.between(LocalDateTime.MIN, rangeEnd));
        } else if (rangeStart != null) {
            builder.and(event.eventDate.between(rangeStart, LocalDateTime.MAX));
        }

        if (builder.getValue() != null) {
            pageEvents = eventRepository.findAll(builder.getValue(), pageRequest);
        } else {
            pageEvents = eventRepository.findAll(pageRequest);
        }

        List<Event> events = pageEvents.getContent();
        setViews(events);
        log.info("The events was found by admin");
        return eventMapper.toListEventFullDto(events);
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(long eventId, EventAdminRequestDto updateEvent) {
        log.info("The beginning of the process of updates a event by admin");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + updateEvent.getCategory()
                            + " was not found"));
            event.setCategory(category);
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("The date and time for which the event is scheduled cannot be " +
                        "earlier than two hours from the current moment");
            } else {
                event.setEventDate(updateEvent.getEventDate());
            }
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            setStateByAdmin(event, updateEvent.getStateAction());
        }

        log.info("The events was update by admin");
        return eventMapper.toEventFullDto(event);
    }

    private void setStateByAdmin(Event event, StateActionAdmin stateActionAdmin) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) &&
                stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            throw new DateException("The start date of the event to be modified must be no earlier " +
                    "than one hour from the date of publication.");
        }

        if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            if (!event.getState().equals(State.PENDING)) {
                throw new RestrictionsViolationException("An event can be published only if it is in the waiting state " +
                        "for publication");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            if (event.getState().equals(State.PUBLISHED)) {
                throw new RestrictionsViolationException("AAn event can be rejected only if it has not been " +
                        "published yet");
            }
            event.setState(State.CANCELED);
        }

    }

    private PageRequest getCustomPage(int from, int size, EventPublicSort sort) {
        if (sort != null) {
            return switch (sort) {
                case EVENT_DATE -> PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
                case VIEWS -> PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "views"));
            };
        } else {
            return PageRequest.of(from, size);
        }

    }

    private List<ViewStatsDto> getViewStats(List<Event> events) {
        List<String> url = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();
        Optional<List<ViewStatsDto>> viewStatsDto = Optional.ofNullable(statClient
                .getStats(LocalDateTime.now().minusYears(20), LocalDateTime.now(), url, true)
        );
        return viewStatsDto.orElse(Collections.emptyList());
    }

    private void setViews(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        Map<String, Long> mapUriAndHits = getViewStats(events).stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));

        for (Event event : events) {
            event.setViews(mapUriAndHits.getOrDefault("/events/" + event.getId(), 0L));
        }
    }
}