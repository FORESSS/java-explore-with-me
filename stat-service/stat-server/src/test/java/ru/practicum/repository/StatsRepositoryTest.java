package ru.practicum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class StatsRepositoryTest {
    @Autowired
    private StatsRepository statsRepository;

    @Test
    void saveEndpointHitTest() {
        EndpointHit hit = new EndpointHit();
        hit.setApp("TestApp");
        hit.setUri("/test");
        hit.setIp("192.168.0.1");
        hit.setTimestamp(LocalDateTime.now());
        EndpointHit savedHit = statsRepository.save(hit);

        assertNotNull(savedHit.getId());
        assertEquals(hit.getApp(), savedHit.getApp());
        assertEquals(hit.getUri(), savedHit.getUri());
        assertEquals(hit.getIp(), savedHit.getIp());
    }

    @Test
    void findViewStatsByUriTest() {
        EndpointHit hit1 = new EndpointHit();
        hit1.setApp("TestApp");
        hit1.setUri("/test");
        hit1.setIp("192.168.0.1");
        hit1.setTimestamp(LocalDateTime.now().minusDays(1));
        statsRepository.save(hit1);

        EndpointHit hit2 = new EndpointHit();
        hit2.setApp("TestApp");
        hit2.setUri("/test");
        hit2.setIp("192.168.0.2");
        hit2.setTimestamp(LocalDateTime.now());
        statsRepository.save(hit2);
        List<ViewStats> stats = statsRepository.findViewStatsByUri(LocalDateTime.now().minusDays(2), LocalDateTime.now(), List.of("/test"));

        assertNotNull(stats);
        assertEquals(1, stats.size());
        assertEquals("/test", stats.get(0).getUri());
    }
}