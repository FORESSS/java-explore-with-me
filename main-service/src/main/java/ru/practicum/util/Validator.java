package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DateTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
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

    public void checkCompilationId(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Подборка событий с id: %d не найдена", compId));
        }
    }

    public Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }

    public void checkCategoryId(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id: %d не найдена", catId));
        }
    }

    public void checkNewCategory(NewCategoryDto newCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(newCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            throw new RestrictionsViolationException(String.format("Категория %s уже создана", newCategoryDto.getName()));
        });
    }

    public void checkCategory(UpdateCategoryDto updateCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(updateCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            throw new RestrictionsViolationException(String.format("Категория %s уже создана", updateCategoryDto.getName()));
        });
    }

    public void checkCategory(long catId) {
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new RestrictionsViolationException("Категория не найдена");
        }
    }

    public User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    public void checkUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
    }

    public void checkEmail(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new RestrictionsViolationException("Email уже используется");
        });
    }

    public Request validateAndGetRequest(long requestId) {
        return requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id: %d не найден", requestId)));
    }

    public void checkRequest(long userId, long eventId) {
        requestsRepository.findByEventIdAndRequesterId(userId, eventId).ifPresent(r -> {
            throw new RestrictionsViolationException("Запрос уже создан");
        });
    }

    public void checkCountRequests(Event event, List<Request> requests) {
        if ((!event.getParticipantLimit().equals(0L)) && (event.getParticipantLimit() == requests.size())) {
            throw new RestrictionsViolationException("Превышено количество запросов");
        }
    }

    public void checkRequestStatus(List<Request> requests) {
        if (requests.stream().map(Request::getStatus).anyMatch(status -> !status.equals(Status.PENDING))) {
            throw new RestrictionsViolationException("Нельзя изменить статус");
        }
    }

    public Event validateAndGetEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
    }

    public void checkEventId(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Событие с id: %d не найдено", eventId));
        }
    }

    public void checkEvent(long userId, long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId).ifPresent(r -> {
            throw new RestrictionsViolationException("Событие уже создано");
        });
    }

    public void checkPublishedEvent(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new RestrictionsViolationException("Событие не опубликовано");
        }
    }

    public void checkDate(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new DateTimeException("Некорректная дата");
        }
    }

    public void publishEvent(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new DateTimeException("Некорректная дата публикации");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new RestrictionsViolationException("Событие должно быть в состоянии ожидания публикации");
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
    }

    public void rejectEvent(Event event) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new RestrictionsViolationException("Опубликованное событие не может быть отклонено");
        }
        event.setState(State.CANCELED);
    }
}