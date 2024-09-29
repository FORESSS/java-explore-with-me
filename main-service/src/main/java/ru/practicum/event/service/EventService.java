package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventById(long userId, long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsByUser(long userId, int from, int size, HttpServletRequest request);

    EventFullDto updateEvent(long userId, long eventId, EventUserRequestDto eventUserRequestDto);

    List<RequestDto> getRequestByEventId(long userId, long eventId);

    RequestStatusDto updateRequestByEventId(long userId, long eventId, RequestUpdateStatusDto requestUpdateStatusDto);

    List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(long eventId, EventAdminRequestDto eventAdminRequestDto);

    List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, boolean onlyAvailable, EventPublicSort sort,
                                           int from, int size, HttpServletRequest request);

    EventFullDto getPublicEventById(long id, HttpServletRequest request);
}