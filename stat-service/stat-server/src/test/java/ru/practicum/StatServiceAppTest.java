package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;

@SpringBootTest
public class StatServiceAppTest {
    @MockBean
    private EndpointHitMapper endpointHitMapper;
    @MockBean
    private ViewStatsMapper viewStatsMapper;

    @Test
    void contextLoads() {
    }
}