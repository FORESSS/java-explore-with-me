package ru.practicum.EndpointHit.service;

import ru.practicum.EndpointHit.model.EndpointHit;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface EndpointHitService {
    void save(EndpointHit endpointHit);

    List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique);
}
