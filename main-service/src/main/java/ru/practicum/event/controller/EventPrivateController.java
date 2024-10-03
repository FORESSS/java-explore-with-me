package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserRequestDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.add(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findById(@PathVariable Long userId, @PathVariable Long eventId,
                                 HttpServletRequest request) {
        return eventService.findById(userId, eventId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        return eventService.findByUser(userId, from, size, request);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody @Valid EventUserRequestDto eventUserRequestDto) {
        return eventService.update(userId, eventId, eventUserRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findRequestByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusDto updateRequestByEventId(@PathVariable Long userId, @PathVariable Long eventId,
                                                   @RequestBody @Valid RequestUpdateStatusDto requestUpdateStatusDto) {
        return eventService.updateRequestByEventId(userId, eventId, requestUpdateStatusDto);
    }
}