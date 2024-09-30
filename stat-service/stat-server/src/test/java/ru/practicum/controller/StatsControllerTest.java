package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatsService statsService;
    @Autowired
    private ObjectMapper objectMapper;
    private EndpointHitDto endpointHitDto;
    private ViewStatsDto viewStatsDto;

    @BeforeEach
    public void setup() {
        endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("test");
        endpointHitDto.setUri("/test-uri");
        endpointHitDto.setIp("192.168.0.1");
        endpointHitDto.setTimestamp(LocalDateTime.now());

        viewStatsDto = new ViewStatsDto();
        viewStatsDto.setApp("test");
        viewStatsDto.setUri("/test-uri");
        viewStatsDto.setHits(5L);
    }

    @Test
    public void saveEndpointHitTest() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitDto)))
                .andExpect(status().isCreated());

        Mockito.verify(statsService).saveEndpointHit(any(EndpointHitDto.class));
    }

    @Test
    public void getViewStatsTest() throws Exception {
        List<ViewStatsDto> viewStatsList = Collections.singletonList(viewStatsDto);
        Mockito.when(statsService.getViewStats(any(String.class), any(String.class), any(List.class), any(Boolean.class)))
                .thenReturn(viewStatsList);

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01T00:00:00")
                        .param("end", "2023-12-31T23:59:59")
                        .param("unique", "true")
                        .param("uris", "/test-uri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is("test")))
                .andExpect(jsonPath("$[0].uri", is("/test-uri")))
                .andExpect(jsonPath("$[0].hits", is(5)));

        Mockito.verify(statsService).getViewStats(any(String.class), any(String.class), any(List.class), any(Boolean.class));
    }
}