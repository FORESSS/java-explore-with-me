package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository endpointHitRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Transactional
    @Override
    public void saveEndpointHit(EndpointHitDto endpointHitDto) {
        log.info("The beginning of the process of creating a statistics record");
        endpointHitRepository.save(endpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("The statistics record has been created");
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("The beginning of the process of obtaining statistics of views");
        List<ViewStats> listViewStats;

        if (CollectionUtils.isEmpty(uris)) {
            uris = endpointHitRepository.findUniqueUri();
        }

        if (unique) {
            listViewStats = endpointHitRepository.findViewStatsByUniqueIp(decodeTime(start),
                    decodeTime(end),
                    uris);
        } else {
            listViewStats = endpointHitRepository.findViewStatsByUri(decodeTime(start),
                    decodeTime(end),
                    uris);
        }

        log.info("Getting the statistics of the views is completed");
        return viewStatsMapper.toListViewStatsDto(listViewStats);
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, Constants.FORMATTER);
    }
}
