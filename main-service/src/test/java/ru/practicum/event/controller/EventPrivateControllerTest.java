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
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserRequestDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPrivateController.class)
public class EventPrivateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper objectMapper;
    private NewEventDto newEventDto;
    private EventFullDto eventFullDto;
    private EventUserRequestDto eventUserRequestDto;
    private RequestUpdateStatusDto requestUpdateStatusDto;

    @BeforeEach
    public void setup() {
        newEventDto = new NewEventDto();
        newEventDto.setAnnotation("annotation");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("description");
        newEventDto.setEventDate(LocalDateTime.now());
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(10L);
        newEventDto.setRequestModeration(true);
        newEventDto.setTitle("title");

        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setAnnotation("annotation");
        eventFullDto.setCategory(new ru.practicum.category.dto.CategoryDto());
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setCreatedOn(LocalDateTime.now());
        eventFullDto.setDescription("description");
        eventFullDto.setEventDate(LocalDateTime.now());
        eventFullDto.setInitiator(new ru.practicum.user.dto.UserShortDto(123L, "123"));
        eventFullDto.setLocation(new ru.practicum.event.model.Location());
        eventFullDto.setPaid(false);
        eventFullDto.setParticipantLimit(10L);
        eventFullDto.setPublishedOn(LocalDateTime.now());
        eventFullDto.setRequestModeration(true);
        eventFullDto.setState(ru.practicum.event.model.State.PENDING);
        eventFullDto.setTitle("title");
        eventFullDto.setViews(0L);

        eventUserRequestDto = new EventUserRequestDto();
        eventUserRequestDto.setAnnotation("annotation");
        eventUserRequestDto.setCategory(1L);
        eventUserRequestDto.setDescription("description");
        eventUserRequestDto.setEventDate(LocalDateTime.now());
        eventUserRequestDto.setPaid(false);
        eventUserRequestDto.setParticipantLimit(10L);
        eventUserRequestDto.setRequestModeration(true);
        eventUserRequestDto.setTitle("title");

        requestUpdateStatusDto = new RequestUpdateStatusDto();
        requestUpdateStatusDto.setRequestIds(Collections.emptySet());
        requestUpdateStatusDto.setStatus(ru.practicum.request.model.Status.CONFIRMED);
    }

    @Test
    public void addEventTest() throws Exception {
        newEventDto.setDescription("Описание события, которое должно быть не менее 20 символов");
        newEventDto.setAnnotation("Аннотация события, которое должно быть не менее 20 символов");
        newEventDto.setLocation(new Location(1L, 10.0f, 20.0f));

        when(eventService.addEvent(anyLong(), any(NewEventDto.class))).thenReturn(eventFullDto);

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated());

        Mockito.verify(eventService).addEvent(anyLong(), any(NewEventDto.class));
    }

    @Test
    public void getEventByIdTest() throws Exception {
        when(eventService.getEventById(anyLong(), anyLong(), any())).thenReturn(eventFullDto);

        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getEventById(anyLong(), anyLong(), any());
    }

    @Test
    public void getEventsByUserTest() throws Exception {
        List<EventShortDto> eventShortDtos = Arrays.asList(new EventShortDto());
        when(eventService.getEventsByUser(anyLong(), anyInt(), anyInt(), any())).thenReturn(eventShortDtos);
        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getEventsByUser(anyLong(), anyInt(), anyInt(), any());
    }

    @Test
    public void updateEventTest() throws Exception {
        eventUserRequestDto.setAnnotation("Annotation of the event, which should be at least 20 characters");
        eventUserRequestDto.setDescription("Description of the event, which should be at least 20 characters");

        when(eventService.updateEvent(anyLong(), anyLong(), any(EventUserRequestDto.class))).thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUserRequestDto)))
                .andExpect(status().isOk());

        Mockito.verify(eventService).updateEvent(anyLong(), anyLong(), any(EventUserRequestDto.class));
    }

    @Test
    public void getRequestByEventIdTest() throws Exception {
        List<RequestDto> requestDtos = Arrays.asList(new RequestDto());
        when(eventService.getRequestsByEventId(anyLong(), anyLong())).thenReturn(requestDtos);

        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getRequestsByEventId(anyLong(), anyLong());
    }

    @Test
    public void updateRequestByEventIdTest() throws Exception {
        RequestStatusDto requestStatusDto = new RequestStatusDto();
        when(eventService.updateRequestByEventId(anyLong(), anyLong(), any(RequestUpdateStatusDto.class))).thenReturn(requestStatusDto);

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateStatusDto)))
                .andExpect(status().isOk());

        Mockito.verify(eventService).updateRequestByEventId(anyLong(), anyLong(), any(RequestUpdateStatusDto.class));
    }
}