package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface StatsService {
    // Метод для сохранения данных о запросах
    void saveEndpointHit(EndpointHitDto endpointHitDto);

    // Метод для поиска статистики по заданным параметрам
    List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique);
}