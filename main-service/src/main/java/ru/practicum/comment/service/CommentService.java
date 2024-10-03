package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto add(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto update(long userId, long eventId, long commentId, NewCommentDto newCommentDto);

    List<CommentDto> findByAuthor(long userId, int from, int size);

    List<CommentDto> findByEvent(long eventId, int from, int size);

    CommentDto findById(long commentId);

    void delete(long userId, long commentId);

    void delete(long commentId);
}