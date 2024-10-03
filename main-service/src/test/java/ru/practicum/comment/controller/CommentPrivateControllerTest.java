package ru.practicum.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentPrivateController.class)
public class CommentPrivateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;
    private NewCommentDto newCommentDto;

    @BeforeEach
    void setup() {
        newCommentDto = new NewCommentDto();
        newCommentDto.setText("Сomment");
    }

    @Test
    void addCommentTest() throws Exception {
        long userId = 1L;
        long eventId = 2L;

        mockMvc.perform(post("/users/{userId}/comments/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isCreated());

        Mockito.verify(commentService).add(userId, eventId, newCommentDto);
    }

    @Test
    void updateCommentTest() throws Exception {
        long userId = 1L;
        long eventId = 2L;
        long commentId = 3L;

        mockMvc.perform(patch("/users/{userId}/comments/{eventId}/{commentId}", userId, eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isOk());

        Mockito.verify(commentService).update(userId, eventId, commentId, newCommentDto);
    }

    @Test
    void deleteCommentTest() throws Exception {
        long userId = 1L;
        long commentId = 2L;

        mockMvc.perform(delete("/users/{userId}/comments/{commentId}", userId, commentId))
                .andExpect(status().isNoContent());

        Mockito.verify(commentService).delete(userId, commentId);
    }
}