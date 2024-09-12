package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    void save(EndpointHit endpointHit);

    List<ViewStats> findByParams(String start, String end, List<String> uris, boolean unique);
}
