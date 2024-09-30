package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
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
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestsRepository requestsRepository;
    private final CompilationRepository compilationRepository;

    public boolean isValidUserId(long userId) {
        return userRepository.existsById(userId);
    }

    public boolean isValidEventId(long eventId) {
        return eventRepository.existsById(eventId);
    }

    public boolean isValidCategoryId(long catId) {
        return categoryRepository.existsById(catId);
    }

    public boolean isValidCompilationId(long compId) {
        return compilationRepository.existsById(compId);
    }

    public boolean checkCategory(Category category) {
        return categoryRepository.findCategoriesByNameContainingIgnoreCase(category.getName().toLowerCase()).isEmpty();
    }

    public boolean checkCategory(long catId, RequestCategoryDto requestCategoryDto) {
        return categoryRepository.findCategoriesByNameContainingIgnoreCase(requestCategoryDto.getName().toLowerCase())
                .stream().noneMatch(c -> c.getId() != catId);
    }

    public boolean checkCategory(long catId) {
        return eventRepository.findAllByCategoryId(catId).isEmpty();
    }

    public boolean checkEmail(User user) {
        return userRepository.findUserByEmail(user.getEmail()).isEmpty();
    }

    public boolean checkRequest(long userId, long eventId) {
        return requestsRepository.findByRequesterIdAndEventId(userId, eventId).isEmpty()
                && eventRepository.findByInitiatorIdAndId(userId, eventId).isEmpty();
    }

    public boolean checkRequestConditions(long eventId) {
        return eventRepository.findById(eventId).orElseThrow().getState().equals(State.PUBLISHED);
    }

    public boolean checkRequestLimit(Event event) {
        List<Request> confirmedRequests = requestsRepository.findAllByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        return event.getParticipantLimit() == 0L || confirmedRequests.size() < event.getParticipantLimit();
    }

    public boolean checkEventDate(LocalDateTime eventDate) {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }

    public boolean checkEventDate(LocalDateTime start, LocalDateTime end) {
        return start == null || end == null || start.isBefore(end);
    }

    public boolean checkEventState(State state) {
        return !state.equals(State.PUBLISHED);
    }

    public boolean checkEventStatus(List<Request> requests) {
        return requests.stream().map(Request::getStatus).allMatch(s -> s.equals(Status.PENDING));
    }

    public boolean checkEventDateForPublish(Event event, StateActionAdmin stateActionAdmin) {
        return !stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT) || event.getEventDate().isAfter(LocalDateTime.now().plusHours(1));
    }

    public boolean checkPublishEvent(Event event) {
        return event.getState().equals(State.PENDING);
    }

    public boolean checkRejectEvent(Event event) {
        return event.getState().equals(State.PUBLISHED);
    }
}