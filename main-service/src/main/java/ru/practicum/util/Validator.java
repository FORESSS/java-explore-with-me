package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class Validator {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestsRepository requestsRepository;
    private final CompilationRepository compilationRepository;

    public void checkUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
    }

    public void checkEventId(long eventId) {
    }

    public void checkCategoryId(long catId) {
    }

    public void checkRequestId(long requestId) {
    }

    public void checkCompilationId(long compId) {
    }

    public User validateAndGetUser(long userId) {
        return null;
    }

    public Event validateAndGetEvent(long eventId) {
        return null;
    }

    public Category validateAndGetCategory(long catId) {
        return null;
    }

    public Request validateAndGetRequest(long requestId) {
        return null;
    }

    public Compilation validateAndGetCompilation(long compId) {
        return null;
    }

    public void checkEmail(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new RestrictionsViolationException("Email уже используется");
        });
    }
}