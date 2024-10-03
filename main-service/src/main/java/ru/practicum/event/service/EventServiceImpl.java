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
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.event.model.QEvent.event;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatClient statClient;
    private final AppConfig appConfig;

    @Override
    @Transactional
    public EventFullDto add(long userId, NewEventDto newEventDto) {
        User initiator = validateAndGetUser(userId);
        Category category = validateAndGetCategory(newEventDto.getCategory());
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
    public EventFullDto findById(long userId, long eventId, HttpServletRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        Event event = validateAndGetEvent(eventId);
        List<ViewStatsDto> viewStats = getViewStats(List.of(event));
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        if (!CollectionUtils.isEmpty(viewStats)) {
            eventFullDto.setViews(viewStats.getFirst().getHits());
        } else {
            eventFullDto.setViews(0L);
        }
        saveHit(request);
        log.info("Получение события с id: {}", eventId);
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findByUser(long userId, int from, int size, HttpServletRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        BooleanExpression byUserId = event.initiator.id.eq(userId);
        Page<Event> pageEvents = eventRepository.findAll(byUserId, pageRequest);
        List<Event> events = pageEvents.getContent();
        setViews(events);
        List<EventShortDto> eventsShortDto = eventMapper.toListEventShortDto(events);
        saveHit(request);
        log.info("Получение списка событий для пользователя с id: {}", userId);
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, EventUserRequestDto eventUserRequestDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        Event event = validateAndGetEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new RestrictionsViolationException("Событие в опубликованном состоянии не может быть изменено");
        }
        if (eventUserRequestDto.getAnnotation() != null && !eventUserRequestDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUserRequestDto.getAnnotation());
        }
        if (eventUserRequestDto.getCategory() != null) {
            Category category = validateAndGetCategory(eventUserRequestDto.getCategory());
            event.setCategory(category);
        }
        if (eventUserRequestDto.getDescription() != null && !eventUserRequestDto.getDescription().isBlank()) {
            event.setDescription(eventUserRequestDto.getDescription());
        }
        if (eventUserRequestDto.getEventDate() != null) {
            if (eventUserRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("Некорректная дата");
            } else {
                event.setEventDate(eventUserRequestDto.getEventDate());
            }
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
    public List<RequestDto> findRequestsByEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Событие с id: %d не найдено", eventId));
        }
        List<Request> requests = requestsRepository.findByEventId(eventId);
        log.info("Получение списка запросов для события с id: {}", eventId);
        return requestMapper.toListRequestDto(requests);
    }

    @Override
    @Transactional
    public RequestStatusDto updateRequestByEventId(long userId, long eventId, RequestUpdateStatusDto requestUpdateStatusDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        Event event = validateAndGetEvent(eventId);
        List<Request> confirmedRequests = requestsRepository.findAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirmedRequests.size()) {
            throw new RestrictionsViolationException("Превышен лимит запросов");
        }
        List<Request> requests = requestsRepository.findByIdIn(requestUpdateStatusDto.getRequestIds());
        if (requests.stream().map(Request::getStatus).anyMatch(status -> !status.equals(Status.PENDING))) {
            throw new RestrictionsViolationException("Статус может быть изменен только для запросов, находящихся в состоянии ожидания");
        }
        requests.forEach(request -> request.setStatus(requestUpdateStatusDto.getStatus()));
        if (requestUpdateStatusDto.getStatus().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + requestUpdateStatusDto.getRequestIds().size());
        }
        log.info("Статус запроса обновлён");
        return requestMapper.toRequestStatusDto(null, requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> findAdminEvents(List<Long> users, State state, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
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
                throw new DateException("Некорректная дата");
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
        log.info("Получение списка событий администратора");
        return eventMapper.toListEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, EventAdminRequestDto eventAdminRequestDto) {
        Event event = validateAndGetEvent(eventId);
        if (eventAdminRequestDto.getAnnotation() != null && !eventAdminRequestDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventAdminRequestDto.getAnnotation());
        }
        if (eventAdminRequestDto.getCategory() != null) {
            Category category = validateAndGetCategory(eventAdminRequestDto.getCategory());
            event.setCategory(category);
        }
        if (eventAdminRequestDto.getDescription() != null && !eventAdminRequestDto.getDescription().isBlank()) {
            event.setDescription(eventAdminRequestDto.getDescription());
        }
        if (eventAdminRequestDto.getEventDate() != null) {
            if (eventAdminRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("Некорректная дата");
            } else {
                event.setEventDate(eventAdminRequestDto.getEventDate());
            }
        }
        if (eventAdminRequestDto.getLocation() != null) {
            event.setLocation(eventAdminRequestDto.getLocation());
        }
        if (eventAdminRequestDto.getPaid() != null) {
            event.setPaid(eventAdminRequestDto.getPaid());
        }
        if (eventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminRequestDto.getParticipantLimit());
        }
        if (eventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminRequestDto.getRequestModeration());
        }
        if (eventAdminRequestDto.getTitle() != null && !eventAdminRequestDto.getTitle().isBlank()) {
            event.setTitle(eventAdminRequestDto.getTitle());
        }
        if (eventAdminRequestDto.getStateAction() != null) {
            setStateByAdmin(event, eventAdminRequestDto.getStateAction());
        }
        log.info("Событие с id: {} обновлено администратором", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, boolean onlyAvailable, EventPublicSort sort,
                                                int from, int size, HttpServletRequest request) {
        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new DateException("Некорректная дата");
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
        saveHit(request);
        log.info("Получение списка событий");
        return eventMapper.toListEventShortDto(events.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findPublicEventById(long id, HttpServletRequest request) {
        Event event = validateAndGetPublishedEvent(id);
        setViews(List.of(event));
        saveHit(request);
        log.info("Получение опубликованного события с id: {}", id);
        return eventMapper.toEventFullDto(event);
    }

    private void setStateByAdmin(Event event, StateActionAdmin stateActionAdmin) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) &&
                stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            throw new DateException("Некорректная дата");
        }
        if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            if (!event.getState().equals(State.PENDING)) {
                throw new RestrictionsViolationException("Событие можно опубликовать только если оно находится в состоянии ожидания");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            if (event.getState().equals(State.PUBLISHED)) {
                throw new RestrictionsViolationException("Событие можно отклонить только если оно не было опубликовано");
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
        return statClient.getStats(LocalDateTime.now().minusYears(20), LocalDateTime.now(), url, true);
    }

    private void setViews(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        Map<String, Long> mapUriAndHits = getViewStats(events).stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
        events.forEach(event -> event.setViews(mapUriAndHits.getOrDefault("/events/" + event.getId(), 0L)));
    }

    private void saveHit(HttpServletRequest request) {
        statClient.saveHit(appConfig.getAppName(), request);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    private Event validateAndGetEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
    }

    private Event validateAndGetPublishedEvent(long eventId) {
        return eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Опубликованное событие с id: %d не найдено", eventId)));
    }

    private Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }
}