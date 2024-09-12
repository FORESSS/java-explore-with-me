package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.service.StatsService;
import ru.practicum.EndpointHitDto;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.ViewStatsDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService endpointHitService;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("hit")
    public void saveEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Received a POST request to save statistics {}", endpointHitDto);
        endpointHitService.saveEndpointHit(endpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @GetMapping("stats")
    public List<ViewStatsDto> getViewStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false) boolean unique) {
        log.info("Received GET request for statistics with parameters start = {}, end = {}, uris = {}, " +
                "unique = {}", start, end, uris, unique);
        List<ViewStats> viewStats = endpointHitService.getViewStats(start, end, uris, unique);
        return viewStatsMapper.toListViewStatsDto(viewStats);
    }
}