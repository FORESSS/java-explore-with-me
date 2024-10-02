package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto add(@PathVariable Long userId, @PathVariable Long eventId,
                          @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.add(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto update(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.update(userId, eventId, commentId, newCommentDto);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    List<CommentDto> findByAuthor(@PathVariable Long userId,
                                  @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentService.findByAuthor(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.delete(userId, commentId);
    }
}