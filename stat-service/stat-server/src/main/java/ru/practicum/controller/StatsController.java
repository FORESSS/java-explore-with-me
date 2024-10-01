package ru.practicum.controller;

import jakarta.validation.Valid;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        statsService.add(endpointHitDto);
    }

    @GetMapping("stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> find(@RequestParam String start, @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(required = false) boolean unique) {
        return statsService.find(start, end, uris, unique);
    }
}