package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.UpdateRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventById(long userId, long eventId);

    List<EventShortDto> getEventsByUser(long userId, int from, int size);

    EventFullDto updateEvent(long userId, long eventId, EventUserRequestDto eventUserRequestDto);

    List<RequestDto> getRequestByEventId(long userId, long eventId);

    RequestStatusUpdateDto updateRequestByEventId(long userId, long eventId, UpdateRequestDto updateRequestDto);

    List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(long eventId, EventAdminRequestDto eventAdminRequestDto);

    List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           boolean onlyAvailable, EventPublicSort sort, int from, int size);

    EventFullDto getPublicEventById(long id);
}