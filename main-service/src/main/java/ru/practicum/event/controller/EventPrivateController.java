package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestUpdateResultDto;
import ru.practicum.requests.dto.UpdateRequestDto;

import java.util.List;

@RestController
@RequestMapping("users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable long userId,
                                     @PathVariable long eventId,
                                     HttpServletRequest request) throws InterruptedException {
        return eventService.getEventById(userId, eventId, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByUser(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        return eventService.getEventsByUser(userId, from, size, request);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestByEventId(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getRequestByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestUpdateResultDto updateRequestByEventId(@PathVariable long userId, @PathVariable long eventId,
                                                         @RequestBody @Valid
                                                         UpdateRequestDto updateRequests) {
        return eventService.updateRequestByEventId(userId, eventId, updateRequests);
    }
}