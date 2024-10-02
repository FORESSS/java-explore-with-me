package ru.practicum.comment.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentPublicController.class)
public class CommentPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;

    @Test
    void findByEventTest() throws Exception {
        long eventId = 1L;
        List<CommentDto> comments = Collections.emptyList();
        Mockito.when(commentService.findByEvent(eventId, 0, 10)).thenReturn(comments);

        mockMvc.perform(get("/comments/event/{eventId}", eventId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        Mockito.verify(commentService).findByEvent(eventId, 0, 10);
    }

    @Test
    void findByIdTest() throws Exception {
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        Mockito.when(commentService.findById(commentId)).thenReturn(commentDto);

        mockMvc.perform(get("/comments/{commentId}", commentId))
                .andExpect(status().isOk());

        Mockito.verify(commentService).findById(commentId);
    }
}