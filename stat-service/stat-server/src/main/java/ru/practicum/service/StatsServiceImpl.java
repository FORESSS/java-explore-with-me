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
    private final StatsRepository endpointHitRepository;
    private final EndpointHitMapper endpointHitMapper; // Add mapper as dependency
    private final ViewStatsMapper viewStatsMapper; // Add mapper as dependency

    @Transactional
    @Override
    public void saveEndpointHit(EndpointHitDto endpointHitDto) {
        // EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        endpointHitRepository.save(endpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique) {
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

        return viewStatsMapper.toListViewStatsDto(listViewStats); // Map to DTO
    }

    private LocalDateTime decodeTime(String time) {
        String decodeTime = URLDecoder.decode(time, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodeTime, Constants.FORMATTER);
    }
}