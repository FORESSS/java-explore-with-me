package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventPublicSort;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPublicController.class)
public class EventPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper objectMapper;
    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;

    @BeforeEach
    public void setup() {
        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setTitle("Event");

        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Event");
    }

    @Test
    public void getAllPublicEventsTest() throws Exception {
        List<EventShortDto> eventShortDtos = Collections.singletonList(eventShortDto);
        Mockito.when(eventService.getAllPublicEvents(any(String.class), any(List.class), any(Boolean.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Boolean.class),
                any(EventPublicSort.class), any(Integer.class), any(Integer.class), any())).thenReturn(eventShortDtos);

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("categories", "1")
                        .param("paid", "true")
                        .param("rangeStart", "2023-01-01T00:00:00")
                        .param("rangeEnd", "2023-12-31T23:59:59")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getAllPublicEvents(any(String.class), any(List.class), any(Boolean.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Boolean.class), any(EventPublicSort.class),
                any(Integer.class), any(Integer.class), any());
    }

    @Test
    public void getPublicEventByIdTest() throws Exception {
        Mockito.when(eventService.getPublicEventById(any(Long.class), any()))
                .thenReturn(eventFullDto);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getPublicEventById(any(Long.class), any());
    }
}