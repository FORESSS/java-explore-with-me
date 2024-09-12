package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("hit")
    @ResponseStatus(HttpStatus.OK)
    public void saveEndpointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getViewStats(@RequestParam String start, @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) @NotNull Boolean unique) {
        return statsService.getViewStats(start, end, uris, unique);
    }
}