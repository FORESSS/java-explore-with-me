package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.IntegrityViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Validator {
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestsRepository requestsRepository;
    private final EventRepository eventRepository;

    public Compilation validateAndGetCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id: %d не найдена", compId)));
    }

    public Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }

    public User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    public void checkEmail(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new IntegrityViolationException("Email уже используется");
        });
    }

    public Request validateAndGetRequest(long requestId) {
        return requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id: %d не найден", requestId)));
    }

    public void checkRequest(long userId, long eventId) {
        requestsRepository.findByEventIdAndRequesterId(userId, eventId).ifPresent(r -> {
            throw new IntegrityViolationException("Запрос уже создан");
        });
    }

    public void checkCountRequests(Event event, List<Request> requests) {
        if ((!event.getParticipantLimit().equals(0L)) && (event.getParticipantLimit() == requests.size())) {
            throw new IntegrityViolationException("Превышено количество запросов");
        }
    }

    public Event validateAndGetEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
    }

    public void checkEvent(long userId, long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId).ifPresent(r -> {
            throw new IntegrityViolationException("Событие уже создано");
        });
    }

    public void checkPublishedEvent(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IntegrityViolationException("Событие не опубликовано");
        }
    }
}