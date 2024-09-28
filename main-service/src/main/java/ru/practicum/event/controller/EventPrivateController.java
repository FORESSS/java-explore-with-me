package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.config.AppConfig;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserRequestDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.UpdateRequestDto;

import java.util.List;

@RestController
@RequestMapping("users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final StatClient statClient;
    private final AppConfig appConfig;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId, HttpServletRequest request) {
        EventFullDto event = eventService.getEventById(userId, eventId);
        statClient.saveHit(appConfig.getAppName(), request);
        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        List<EventShortDto> events = eventService.getEventsByUser(userId, from, size);
        statClient.saveHit(appConfig.getAppName(), request);
        return events;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody @Valid EventUserRequestDto eventUserRequestDto) {
        return eventService.updateEvent(userId, eventId, eventUserRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getRequestByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateDto updateRequestByEventId(@PathVariable Long userId, @PathVariable Long eventId,
                                                         @RequestBody @Valid UpdateRequestDto updateRequestDto) {
        return eventService.updateRequestByEventId(userId, eventId, updateRequestDto);
    }
}