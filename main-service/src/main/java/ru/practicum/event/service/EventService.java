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
    EventFullDto add(long userId, NewEventDto newEventDto);

    EventFullDto findById(long userId, long eventId, HttpServletRequest request);

    List<EventShortDto> findByUser(long userId, int from, int size, HttpServletRequest request);

    EventFullDto update(long userId, long eventId, EventUserRequestDto eventUserRequestDto);

    List<RequestDto> findRequestsByEventId(long userId, long eventId);

    RequestStatusDto updateRequestByEventId(long userId, long eventId, RequestUpdateStatusDto requestUpdateStatusDto);

    List<EventFullDto> findAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(long eventId, EventAdminRequestDto eventAdminRequestDto);

    List<EventShortDto> findPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, boolean onlyAvailable, EventPublicSort sort,
                                         int from, int size, HttpServletRequest request);

    EventFullDto findPublicEventById(long id, HttpServletRequest request);
}