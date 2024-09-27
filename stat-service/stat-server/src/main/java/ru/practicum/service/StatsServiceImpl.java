package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;
import ru.practicum.stat_util.Constants;
import ru.practicum.stat_util.StatValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    @Transactional
    public void saveEndpointHit(EndpointHitDto endpointHitDto) {
        log.info("Получен запрос для сохранения статистики");
        statsRepository.save(endpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = parseTime(start);
        LocalDateTime endTime = parseTime(end);
        StatValidator.checkDateTime(startTime, endTime);
        if (CollectionUtils.isEmpty(uris)) {
            uris = statsRepository.findUniqueUri();
        }
        List<ViewStats> listViewStats;
        if (unique) {
            listViewStats = statsRepository.findViewStatsByUniqueIp(startTime, endTime, uris);
        } else {
            listViewStats = statsRepository.findViewStatsByUri(startTime, endTime, uris);
        }
        log.info("Получение статистики");
        return viewStatsMapper.toListViewStatsDto(listViewStats);
    }

    private LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, Constants.FORMATTER);
    }
}