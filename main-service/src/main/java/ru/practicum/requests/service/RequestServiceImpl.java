package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.Validator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final RequestMapper requestMapper;
    private final Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequests(long userId) {
        validator.validateAndGetUser(userId);
        List<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("Получение списка всех пользователей");
        return requestMapper.toListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        validator.checkRequest(userId, eventId);
        validator.checkEvent(userId, eventId);
        User user = validator.validateAndGetUser(userId);
        Event event = validator.validateAndGetEvent(eventId);
        validator.checkPublishedEvent(event);
        List<Request> confirmedRequests = requestsRepository.findAllByStatusAndEventId(Status.CONFIRMED, eventId);
        validator.checkCountRequests(event, confirmedRequests);
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);
        if ((event.getParticipantLimit().equals(0L)) || (!event.getRequestModeration())) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        request = requestsRepository.save(request);
        log.info("Запрос пользователя с id: {} добавлен", userId);
        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        validator.validateAndGetUser(userId);
        Request request = validator.validateAndGetRequest(requestId);
        request.setStatus(Status.CANCELED);
        log.info("Запрос пользователя с id: {} отменён", userId);
        return requestMapper.toParticipationRequestDto(request);
    }
}