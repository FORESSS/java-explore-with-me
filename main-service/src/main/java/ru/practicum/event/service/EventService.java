package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.UpdateRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto getEventById(long userId, long eventId, HttpServletRequest request);

    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUser(long userId, int from, int size, HttpServletRequest request);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestByEventId(long userId, long eventId);

    RequestUpdateResultDto updateRequestByEventId(long userId, long eventId, UpdateRequestDto updateRequest);

    List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, boolean onlyAvailable, EventPublicSort sort,
                                           int from, int size, HttpServletRequest request);

    EventFullDto getPublicEventById(long id, HttpServletRequest request);
}