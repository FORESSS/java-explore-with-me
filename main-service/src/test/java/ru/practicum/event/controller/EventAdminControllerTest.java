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
import ru.practicum.event.dto.EventAdminRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Arrays;
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
        eventFullDto.setCategory(new ru.practicum.category.dto.CategoryDto());
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setCreatedOn(LocalDateTime.now());
        eventFullDto.setDescription("Description");
        eventFullDto.setEventDate(LocalDateTime.now());
        eventFullDto.setInitiator(new ru.practicum.user.dto.UserShortDto(123L, "123"));
        eventFullDto.setLocation(new ru.practicum.event.model.Location());
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
        eventAdminRequestDto.setAnnotation("Annotation" .repeat(20)); // Длина аннотации должна быть не менее 20 символов
        eventAdminRequestDto.setCategory(1L);
        eventAdminRequestDto.setDescription("Description" .repeat(20)); // Длина описания должна быть не менее 20 символов
        eventAdminRequestDto.setEventDate(LocalDateTime.now());
        eventAdminRequestDto.setPaid(true);
        eventAdminRequestDto.setParticipantLimit(10L);
        eventAdminRequestDto.setRequestModeration(true);
        eventAdminRequestDto.setTitle("Title");

        when(eventService.updateEventAdmin(anyLong(), any(EventAdminRequestDto.class)))
                .thenReturn(eventFullDto);

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventAdminRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        Mockito.verify(eventService).updateEventAdmin(anyLong(), any(EventAdminRequestDto.class));
    }

    @Test
    public void getAllAdminEventsTest() throws Exception {
        List<EventFullDto> eventFullDtos = Arrays.asList(eventFullDto);
        when(eventService.getAllAdminEvents(any(List.class), any(State.class), any(List.class), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt()))
                .thenReturn(eventFullDtos);

        mockMvc.perform(get("/admin/events")
                        .param("users", "1,2,3") // Передаем список пользователей
                        .param("state", "PUBLISHED") // Передаем состояние
                        .param("categories", "1,2,3") // Передаем список категорий
                        .param("rangeStart", "2024-01-01T00:00:00") // Передаем начальную дату
                        .param("rangeEnd", "2024-12-31T23:59:59") // Передаем конечную дату
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getAllAdminEvents(any(List.class), any(State.class), any(List.class), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt());
    }
}