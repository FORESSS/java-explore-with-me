package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.servlet.http.HttpServletRequest;
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
import ru.practicum.config.AppConfig;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.UpdateRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
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
    private final RequestsRepository requestsRepository;
    private final Validator validator;
    private final StatClient statClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final AppConfig appConfig;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long userId, long eventId, HttpServletRequest request) {
        validator.checkUserId(userId);
        Event event = validator.validateAndGetEvent(eventId);
        List<ViewStatsDto> viewStats = getViewStats(List.of(event));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        if (!CollectionUtils.isEmpty(viewStats)) {
            eventFullDto.setViews(viewStats.getFirst().getHits());
        } else {
            eventFullDto.setViews(0L);
        }
        statClient.saveHit(appConfig.getAppName(), request);
        log.info("Получение события с id: {}", eventId);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        User initiator = validator.validateAndGetUser(userId);
        Category category = validator.validateAndGetCategory(newEventDto.getCategory());
        validator.checkDate(LocalDateTime.now().plusHours(1), newEventDto.getEventDate());
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
        Event event = eventRepository.save(newEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setViews(0L);
        log.info("Создано событие с id: {}", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(long userId, int from, int size, HttpServletRequest request) {
        validator.checkUserId(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        BooleanExpression byUserId = event.initiator.id.eq(userId);
        Page<Event> pageEvents = eventRepository.findAll(byUserId, pageRequest);
        List<Event> events = pageEvents.getContent();
        setViews(events);
        List<EventShortDto> eventsShortDto = eventMapper.toListEventShortDto(events);
        log.info("Получение событий пользователя с id: {}", userId);
        statClient.saveHit(appConfig.getAppName(), request);
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        validator.checkUserId(userId);
        Event event = validator.validateAndGetEvent(eventId);
        validator.checkPublishedEvent(event);
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = validator.validateAndGetCategory(updateEvent.getCategory());
            event.setCategory(category);
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            validator.checkDate(LocalDateTime.now().plusHours(1), updateEvent.getEventDate());
            event.setEventDate(updateEvent.getEventDate());
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
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
            }
        }
        log.info("Событие с id: {} обновлено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestByEventId(long userId, long eventId) {
        validator.checkUserId(userId);
        validator.checkEventId(eventId);
        List<Request> requests = requestsRepository.findByEventId(eventId);
        log.info("Получение информации о запросах на участие в событии пользователя с id: {}", userId);
        return requestMapper.toListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public RequestUpdateResultDto updateRequestByEventId(long userId, long eventId, UpdateRequestDto updateRequests) {
        validator.checkUserId(userId);
        Event event = validator.validateAndGetEvent(eventId);
        List<Request> confirmedRequests = requestsRepository.findAllByStatusAndEventId(Status.CONFIRMED, eventId);
        validator.checkCountRequests(event, confirmedRequests);
        List<Request> requests = requestsRepository.findByIdIn(updateRequests.getRequestIds());
        validator.checkRequestStatus(requests);
        requests.forEach(request -> request.setStatus(updateRequests.getStatus()));
        if (updateRequests.getStatus().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + updateRequests.getRequestIds().size());
        }
        log.info("Статус запроса изменён");
        return requestMapper.toEventRequestStatusResult(null, requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        BooleanBuilder builder = createEventFilter(users, state, categories, rangeStart, rangeEnd, null, null);
        PageRequest pageRequest = getCustomPage(from, size, null);
        assert builder.getValue() != null;
        Page<Event> pageEvents = eventRepository.findAll(builder.getValue(), pageRequest);
        List<Event> events = pageEvents.getContent();
        setViews(events);
        log.info("Получение событий администратора");
        return eventMapper.toListEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = validator.validateAndGetEvent(eventId);
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = validator.validateAndGetCategory(updateEvent.getCategory());
            event.setCategory(category);
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            validator.checkDate(LocalDateTime.now().plusHours(1), updateEvent.getEventDate());
            event.setEventDate(updateEvent.getEventDate());
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
        log.info("Событие с id: {} обновлено", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, boolean onlyAvailable, EventPublicSort sort,
                                                  int from, int size, HttpServletRequest request) {
        BooleanBuilder builder = createEventFilter(null, null, categories, rangeStart, rangeEnd, onlyAvailable, text);
        PageRequest pageRequest = getCustomPage(from, size, sort);
        assert builder.getValue() != null;
        Page<Event> events = eventRepository.findAll(builder.getValue(), pageRequest);
        setViews(events.getContent());
        statClient.saveHit(appConfig.getAppName(), request);
        log.info("Получение публичных событий");
        return eventMapper.toListEventShortDto(events.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(long id, HttpServletRequest request) {
        validator.checkEventId(id);
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED).orElseThrow();
        setViews(List.of(event));
        statClient.saveHit(appConfig.getAppName(), request);
        log.info("Получение события с id: {}", id);
        return eventMapper.toEventFullDto(event);
    }

    private void setStateByAdmin(Event event, StateActionAdmin stateActionAdmin) {
        if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            validator.publishEvent(event);
        } else if (stateActionAdmin.equals(StateActionAdmin.REJECT_EVENT)) {
            validator.rejectEvent(event);
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

    private BooleanBuilder createEventFilter(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, String text) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!CollectionUtils.isEmpty(users) && !users.contains(0L)) {
            builder.and(event.initiator.id.in(users));
        }
        if (state != null) {
            builder.and(event.state.eq(state));
        }
        if (!CollectionUtils.isEmpty(categories)) {
            builder.and(event.category.id.in(categories));
        }
        if (rangeStart != null || rangeEnd != null) {
            if (rangeStart == null) {
                rangeStart = LocalDateTime.MIN;
            }
            if (rangeEnd == null) {
                rangeEnd = LocalDateTime.MAX;
            }
            validator.checkDate(rangeStart, rangeEnd);
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            builder.and(event.participantLimit.eq(0L)
                    .or(event.participantLimit.gt(event.confirmedRequests)));
        }
        if (text != null) {
            builder.and(event.annotation.containsIgnoreCase(text.toLowerCase())
                    .or(event.description.containsIgnoreCase(text.toLowerCase())));
        }
        return builder;
    }
}