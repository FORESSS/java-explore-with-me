package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.RequestCategoryDto;
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
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Событие с id: %d не найдено", eventId));
        }
    }

    public void checkCategoryId(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id: %d не найдена", catId));
        }
    }

    public void checkRequestId(long requestId) {
        if (!requestsRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Запрос с id: %d не найден", requestId));
        }
    }

    public void checkCompilationId(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Подборка с id: %d не найдена", compId));
        }
    }

    public User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    public Event validateAndGetEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
    }

    public Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }

    public Request validateAndGetRequest(long requestId) {
        return requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id: %d не найден", requestId)));
    }

    public Compilation validateAndGetCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id: %d не найдена", compId)));
    }

    public void checkEmail(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new RestrictionsViolationException("Email уже используется");
        });
    }

    public void checkCategory(Category category) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(category.getName().toLowerCase()).ifPresent(c -> {
            throw new RestrictionsViolationException(String.format("Категория с названием: %s уже существует", category.getName()));
        });
    }

    public void checkCategory(long catId, RequestCategoryDto requestCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(
                requestCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            if (c.getId() != catId) {
                throw new RestrictionsViolationException(String.format("Категория с названием: %s уже существует", requestCategoryDto.getName()));
            }
        });
    }

    public void checkCategory(long catId) {
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new RestrictionsViolationException(String.format("Категория c id: %d уже существует", catId));
        }
    }
}