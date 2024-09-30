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
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserRequestDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.model.Status;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
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
        newEventDto.setAnnotation("Annotation");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Description");
        newEventDto.setEventDate(LocalDateTime.now());
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(10L);
        newEventDto.setRequestModeration(true);
        newEventDto.setTitle("Title");

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
        eventFullDto.setPaid(false);
        eventFullDto.setParticipantLimit(10L);
        eventFullDto.setPublishedOn(LocalDateTime.now());
        eventFullDto.setRequestModeration(true);
        eventFullDto.setState(State.PENDING);
        eventFullDto.setTitle("title");
        eventFullDto.setViews(0L);

        eventUserRequestDto = new EventUserRequestDto();
        eventUserRequestDto.setAnnotation("Annotation");
        eventUserRequestDto.setCategory(1L);
        eventUserRequestDto.setDescription("Description");
        eventUserRequestDto.setEventDate(LocalDateTime.now());
        eventUserRequestDto.setPaid(false);
        eventUserRequestDto.setParticipantLimit(10L);
        eventUserRequestDto.setRequestModeration(true);
        eventUserRequestDto.setTitle("Title");

        requestUpdateStatusDto = new RequestUpdateStatusDto();
        requestUpdateStatusDto.setRequestIds(Collections.emptySet());
        requestUpdateStatusDto.setStatus(Status.CONFIRMED);
    }

    @Test
    public void addEventTest() throws Exception {
        newEventDto.setDescription("12345 12345 12345 12345");
        newEventDto.setAnnotation("12345 12345 12345 12345");
        newEventDto.setLocation(new Location(1L, 55.86f, 37.32f));
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
        List<EventShortDto> eventShortDtos = List.of(new EventShortDto());
        when(eventService.getEventsByUser(anyLong(), anyInt(), anyInt(), any())).thenReturn(eventShortDtos);

        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(eventService).getEventsByUser(anyLong(), anyInt(), anyInt(), any());
    }

    @Test
    public void updateEventTest() throws Exception {
        eventUserRequestDto.setAnnotation("12345 12345 12345 12345");
        eventUserRequestDto.setDescription("12345 12345 12345 12345");
        when(eventService.updateEvent(anyLong(), anyLong(), any(EventUserRequestDto.class))).thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUserRequestDto)))
                .andExpect(status().isOk());

        Mockito.verify(eventService).updateEvent(anyLong(), anyLong(), any(EventUserRequestDto.class));
    }

    @Test
    public void getRequestByEventIdTest() throws Exception {
        List<RequestDto> requestDtos = List.of(new RequestDto());
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