package ru.practicum.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationAdminController.class)
public class CompilationAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationService compilationService;
    @Autowired
    private ObjectMapper objectMapper;
    private NewCompilationDto newCompilationDto;
    private UpdateCompilationDto updateCompilationDto;
    private CompilationDto compilationDto;
    private EventShortDto eventShortDto;

    @BeforeEach
    public void setup() {
        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setEvents(List.of(1L, 2L, 3L));
        newCompilationDto.setPinned(true);
        newCompilationDto.setTitle("Compilation");

        updateCompilationDto = new UpdateCompilationDto();
        updateCompilationDto.setEvents(List.of(1L, 2L, 3L));
        updateCompilationDto.setPinned(true);
        updateCompilationDto.setTitle("Updated Compilation");

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
    public void addCompilationTest() throws Exception {
        Mockito.when(compilationService.addCompilation(any(NewCompilationDto.class)))
                .thenReturn(compilationDto);

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.events[0].id", is(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is("Compilation")));

        Mockito.verify(compilationService).addCompilation(any(NewCompilationDto.class));
    }

    @Test
    public void updateCompilationTest() throws Exception {
        Mockito.when(compilationService.updateCompilation(any(Long.class), any(UpdateCompilationDto.class)))
                .thenReturn(compilationDto);

        mockMvc.perform(patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCompilationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.events[0].id", is(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is("Compilation")));

        Mockito.verify(compilationService).updateCompilation(any(Long.class), any(UpdateCompilationDto.class));
    }

    @Test
    public void deleteCompilationTest() throws Exception {
        mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(compilationService).deleteCompilation(any(Long.class));
    }
}