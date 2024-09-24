package ru.practicum.EndpointHit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.Constants;
import ru.practicum.EndpointHit.mapper.EndpointHitMapper;
import ru.practicum.EndpointHit.model.EndpointHit;
import ru.practicum.EndpointHit.repository.EndpointHitRepository;
import ru.practicum.ViewStats.mapper.ViewStatsMapper;
import ru.practicum.ViewStats.model.ViewStats;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Transactional
    @Override
    public void save(EndpointHit endpointHit) {
        log.info("The beginning of the process of creating a statistics record");
        endpointHitRepository.save(endpointHit);
        log.info("The statistics record has been created");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique) {
        List<ViewStats> listViewStats;
        if (CollectionUtils.isEmpty(uris)) {
            uris = endpointHitRepository.findUniqueUri();
        }
        if (unique) {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUriAndUniqueIp(parseTime(start),
                    parseTime(end),
                    uris);
        } else {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUri(parseTime(start),
                    parseTime(end),
                    uris);
        }
        log.info("Получение статистики");
        return viewStatsMapper.listViewStatsToListViewStatsDto(listViewStats);
    }

    private LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, Constants.FORMATTER);
    }
}
