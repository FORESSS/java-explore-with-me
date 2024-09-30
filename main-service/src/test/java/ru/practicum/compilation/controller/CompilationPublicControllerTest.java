package ru.practicum.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.EventShortDto;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationPublicController.class)
public class CompilationPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationService compilationService;
    @Autowired
    private ObjectMapper objectMapper;
    private CompilationDto compilationDto;
    private EventShortDto eventShortDto;

    @BeforeEach
    public void setup() {
        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Event");

        compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setEvents(List.of(eventShortDto));
        compilationDto.setPinned(true);
        compilationDto.setTitle("Compilation");
    }

    @Test
    public void getAllCompilationsTest() throws Exception {
        Mockito.when(compilationService.getAllCompilations(eq(null), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/compilations")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        Mockito.verify(compilationService).getAllCompilations(eq(null), anyInt(), anyInt());
    }

    @Test
    public void getCompilationByIdTest() throws Exception {
        Mockito.when(compilationService.getCompilationById(anyLong()))
                .thenReturn(compilationDto);

        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.events[0].id", is(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is("Compilation")));

        Mockito.verify(compilationService).getCompilationById(anyLong());
    }
}