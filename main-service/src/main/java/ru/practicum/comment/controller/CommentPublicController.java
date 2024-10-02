package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/event/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    List<CommentDto> findByEvent(@PathVariable Long eventId,
                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentService.findByEvent(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    CommentDto findById(@PathVariable Long commentId) {
        return commentService.findById(commentId);
    }
}