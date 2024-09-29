package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    EventFullDto findEventById(long userId, long eventId);

    List<EventShortDto> findEventsByUser(long userId, int from, int size);

    EventFullDto updateEvent(EventUserRequestDto updateEventUserRequest, long userId, long eventId);

    List<RequestDto> findRequestByEventId(long userId, long eventId);

    RequestStatusDto updateRequestByEventId(RequestUpdateStatusDto updateRequest,
                                            long userId,
                                            long eventId);

    List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(long eventId, EventAdminRequestDto eventAdminRequestDto);

    List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           boolean onlyAvailable, EventPublicSort sort, int from, int size);

    EventFullDto getPublicEventById(long id);
}