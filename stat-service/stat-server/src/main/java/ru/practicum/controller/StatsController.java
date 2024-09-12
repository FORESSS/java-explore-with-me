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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("hit")
    public void saveEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("stats")
    public List<ViewStatsDto> findByParams(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) @NotNull Boolean unique) {
        return statsService.getViewStats(start, end, uris, unique);
    }
}