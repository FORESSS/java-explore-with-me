package ru.practicum.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Status;
import ru.practicum.request.service.RequestService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestPrivateController.class)
public class RequestPrivateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper objectMapper;
    private RequestDto requestDto;

    @BeforeEach
    public void setup() {
        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setEvent(1L);
        requestDto.setRequester(1L);
        requestDto.setStatus(Status.PENDING);
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        List<RequestDto> requestDtos = Arrays.asList(requestDto);
        Mockito.when(requestService.getAllRequests(any(Long.class)))
                .thenReturn(requestDtos);
        mockMvc.perform(get("/users/1/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].event", is(1)))
                .andExpect(jsonPath("$[0].requester", is(1)));

        Mockito.verify(requestService).getAllRequests(any(Long.class));
    }

    @Test
    public void addRequestTest() throws Exception {
        Mockito.when(requestService.addRequest(any(Long.class), any(Long.class)))
                .thenReturn(requestDto);
        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.requester", is(1)));

        Mockito.verify(requestService).addRequest(any(Long.class), any(Long.class));
    }

    @Test
    public void cancelRequestTest() throws Exception {
        Mockito.when(requestService.cancelRequest(any(Long.class), any(Long.class)))
                .thenReturn(requestDto);
        mockMvc.perform(patch("/users/1/requests/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.requester", is(1)));

        Mockito.verify(requestService).cancelRequest(any(Long.class), any(Long.class));
    }
}