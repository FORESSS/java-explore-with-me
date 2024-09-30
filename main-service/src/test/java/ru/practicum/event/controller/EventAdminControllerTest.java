package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.dto.EventAdminRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventAdminController.class)
public class EventAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper objectMapper;
    private EventAdminRequestDto eventAdminRequestDto;
    private EventFullDto eventFullDto;

    @BeforeEach
    public void setup() {
        eventAdminRequestDto = new EventAdminRequestDto();
        eventAdminRequestDto.setAnnotation("Annotation");
        eventAdminRequestDto.setCategory(1L);
        eventAdminRequestDto.setDescription("Description");
        eventAdminRequestDto.setEventDate(LocalDateTime.now());
        eventAdminRequestDto.setPaid(true);
        eventAdminRequestDto.setParticipantLimit(10L);
        eventAdminRequestDto.setRequestModeration(true);
        eventAdminRequestDto.setTitle("Title");

        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setAnnotation("Annotation");
        eventFullDto.setCategory(new CategoryDto());
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setCreatedOn(LocalDateTime.now());
        eventFullDto.setDescription("Description");
        eventFullDto.setEventDate(LocalDateTime.now());
        eventFullDto.setInitiator(new UserShortDto());
        eventFullDto.setLocation(new Location());
        eventFullDto.setPaid(true);
        eventFullDto.setParticipantLimit(10L);
        eventFullDto.setPublishedOn(LocalDateTime.now());
        eventFullDto.setRequestModeration(true);
        eventFullDto.setState(State.PUBLISHED);
        eventFullDto.setTitle("Title");
        eventFullDto.setViews(0L);
    }

    @Test
    public void updateEventAdminTest() throws Exception {
        EventAdminRequestDto eventAdminRequestDto = new EventAdminRequestDto();
        eventAdminRequestDto.setAnnotation("12345 12345 12345 12345");
        eventAdminRequestDto.setCategory(1L);
        eventAdminRequestDto.setDescription("12345 12345 12345 12345");
        eventAdminRequestDto.setEventDate(LocalDateTime.now());
        eventAdminRequestDto.setPaid(true);
        eventAdminRequestDto.setParticipantLimit(10L);
        eventAdminRequestDto.setRequestModeration(true);
        eventAdminRequestDto.setTitle("Title");

        when(eventService.updateEventByAdmin(anyLong(), any(EventAdminRequestDto.class)))
                .thenReturn(eventFullDto);

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventAdminRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        Mockito.verify(eventService).updateEventByAdmin(anyLong(), any(EventAdminRequestDto.class));
    }

    @Test
    public void getAllAdminEventsTest() throws Exception {
        List<EventFullDto> eventFullDtos = Collections.singletonList(eventFullDto);
        when(eventService.findAdminEvents(any(List.class), any(State.class), any(List.class), any(LocalDateTime.class),
                any(LocalDateTime.class), anyInt(), anyInt())).thenReturn(eventFullDtos);

        mockMvc.perform(get("/admin/events")
                        .param("users", "1,2,3")
                        .param("state", "PUBLISHED")
                        .param("categories", "1,2,3")
                        .param("rangeStart", "2024-01-01T00:00:00")
                        .param("rangeEnd", "2024-12-31T23:59:59")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).findAdminEvents(any(List.class), any(State.class), any(List.class),
                any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt());
    }
}