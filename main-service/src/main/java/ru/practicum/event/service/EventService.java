package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.UpdateRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    EventFullDto findEventById(long userId, long eventId);

    List<EventShortDto> findEventsByUser(long userId, int from, int size);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, long userId, long eventId);

    List<ParticipationRequestDto> findRequestByEventId(long userId, long eventId);

    RequestUpdateResultDto updateRequestByEventId(UpdateRequestDto updateRequest,
                                                  long userId,
                                                  long eventId);

    List<EventShortDto> getAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           boolean onlyAvailable, EventPublicSort sort, int from, int size);

    EventFullDto getPublicEventById(long id);

    List<EventFullDto> getAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId);
}
