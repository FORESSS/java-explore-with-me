package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
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
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(long userId) {
        validator.checkUserId(userId);
        List<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("Получение всех запросов пользователя с id: {}", userId);
        return requestMapper.toListRequestDto(requests);
    }

    @Override
    @Transactional
    public RequestDto addRequest(long userId, long eventId) {
        validator.checkRequest(userId, eventId);
        User user = validator.validateAndGetUser(userId);
        Event event = validator.validateAndGetEvent(eventId);
        validator.checkRequestLimit(eventId);
        validator.checkRequestCreationConditions(userId, eventId);
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
    public RequestDto cancelRequest(long userId, long requestId) {
        log.info("The beginning of the process of canceling a request");
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id = " + userId + " not found"));
        Request request = requestsRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                "Request with id = " + requestId + " not found"
        ));
        request.setStatus(Status.CANCELED);
        log.info("The request has been canceled");
        return requestMapper.toRequestDto(request);
    }
}