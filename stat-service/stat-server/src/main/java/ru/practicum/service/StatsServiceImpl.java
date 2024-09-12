package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.Constants;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper; // Add mapper as dependency
    private final ViewStatsMapper viewStatsMapper; // Add mapper as dependency

    @Transactional
    @Override
    public void save(EndpointHitDto endpointHitDto) {

        statsRepository.save(endpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique) {
        List<ViewStats> listViewStats;

        if (CollectionUtils.isEmpty(uris)) {
            uris = statsRepository.findUniqueUri();
        }
        if (unique) {
            listViewStats = statsRepository.findViewStatsByUniqueIp(decodeTime(start),
                    decodeTime(end),
                    uris);
        } else {
            listViewStats = statsRepository.findViewStatsByUri(decodeTime(start),
                    decodeTime(end),
                    uris);
        }

        return viewStatsMapper.toListViewStatsDto(listViewStats);
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, Constants.FORMATTER);
    }
}