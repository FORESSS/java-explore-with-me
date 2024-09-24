package ru.practicum.EndpointHit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.EndpointHit.model.EndpointHit;
import ru.practicum.EndpointHit.repository.EndpointHitRepository;
import ru.practicum.ViewStats.mapper.ViewStatsMapper;
import ru.practicum.ViewStats.model.ViewStats;
import ru.practicum.ViewStatsDto;
import ru.practicum.utils.Constants;
import ru.practicum.utils.DateTimeException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final ViewStatsMapper viewStatsMapper;

    @Transactional
    @Override
    public void save(EndpointHit endpointHit) {
        log.info("The beginning of the process of creating a statistics record");
        endpointHitRepository.save(endpointHit);
        log.info("The statistics record has been created");
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique) {
        log.info("The beginning of the process of obtaining statistics of views");
        List<ViewStats> listViewStats;
        LocalDateTime startTime = decodeTime(start);
        LocalDateTime endTime = decodeTime(end);

        if (startTime.isAfter(endTime)) {
            throw new DateTimeException("5665665");
        }

        if (CollectionUtils.isEmpty(uris)) {
            uris = endpointHitRepository.findUniqueUri();
        }

        if (unique) {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUriAndUniqueIp(startTime,
                    endTime,
                    uris);
        } else {
            listViewStats = endpointHitRepository.findViewStatsByStartAndEndAndUri(startTime,
                    endTime,
                    uris);
        }

        log.info("Getting the statistics of the views is completed");
        return viewStatsMapper.listViewStatsToListViewStatsDto(listViewStats);
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, Constants.FORMATTER);
    }
}