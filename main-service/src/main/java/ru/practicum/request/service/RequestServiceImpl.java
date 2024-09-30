package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Validator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> find(long userId) {
        if (!validator.isValidUserId(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        List<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("Получение всех запросов пользователя с id: {}", userId);
        return requestMapper.toListRequestDto(requests);
    }

    @Override
    @Transactional
    public RequestDto add(long userId, long eventId) {
        if (!validator.checkRequest(userId, eventId)) {
            throw new RestrictionsViolationException(String.format(
                    "Запрос пользователя с id: %d для события с id: %d уже существует", userId, eventId));
        }
        User user = validateAndGetUser(userId);
        Event event = validateAndGetEvent(eventId);
        if (validator.checkRequestLimit(event)) {
            throw new RestrictionsViolationException("Превышен лимит запросов");
        }
        if (!validator.checkRequestConditions(eventId)) {
            throw new RestrictionsViolationException(String.format("Событие с id: %d не опубликовано", eventId));
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);
        if ((event.getParticipantLimit().equals(0L)) || (!event.getRequestModeration())) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        requestsRepository.save(request);
        log.info("Запрос с id: {} создан", request.getId());
        return requestMapper.toRequestDto(request);
    }

    @Override
    @Transactional
    public RequestDto cancel(long userId, long requestId) {
        if (validator.isValidUserId(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        Request request = validateAndGetRequest(userId);
        request.setStatus(Status.CANCELED);
        log.info("Запрос с id: {} отменён", requestId);
        return requestMapper.toRequestDto(request);
    }

    public User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    private Event validateAndGetEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
    }

    private Request validateAndGetRequest(long requestId) {
        return requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id: %d не найден", requestId)));
    }
}