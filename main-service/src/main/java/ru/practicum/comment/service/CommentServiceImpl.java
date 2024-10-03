package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto add(long userId, long eventId, NewCommentDto newCommentDto) {
        User author = validateAndGetUser(userId);
        Event event = validateAndGetPublishedEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Опубликованное событие с id: %d не найдено", eventId));
        }
        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        log.info("Сохраняем комментарий с полями: author={}, event={}, created={}", comment.getAuthor(), comment.getEvent(), comment.getCreated());
        commentRepository.save(comment);
        log.info("Комментарий с id: {} добавлен пользователем с id: {} к событию с id: {}", comment.getId(), userId, eventId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(long userId, long eventId, long commentId, NewCommentDto newCommentDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        Event event = validateAndGetPublishedEvent(eventId);
        Comment comment = validateAndGetComment(commentId);
        if (comment.getEvent() != event) {
            throw new RestrictionsViolationException("Комментарий другого события");
        }
        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        log.info("Комментарий с id: {} обновлен пользователем с id: {}", commentId, userId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByAuthor(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size)).getContent();
        log.info("Получение комментариев автора с id: {}", userId);
        return commentMapper.toListCommentDto(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByEvent(long eventId, int from, int size) {
        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED)) {
            throw new NotFoundException(String.format("Опубликованное событие с id: %d не найдено", eventId));
        }
        List<Comment> comments = commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size)).getContent();
        log.info("Получение комментариев для события с id: {}", eventId);
        return commentMapper.toListCommentDto(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(long commentId) {
        Comment comment = validateAndGetComment(commentId);
        log.info("Получение комментария с id: {}", commentId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void delete(long userId, long commentId) {
        User author = validateAndGetUser(userId);
        Comment comment = validateAndGetComment(commentId);
        if (comment.getAuthor() != author) {
            throw new RestrictionsViolationException("Только автор может удалить комментарий");
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id: {} удален автором с id: {}", commentId, userId);
    }

    @Override
    @Transactional
    public void delete(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format("Комментарий с id: %d не найден", commentId));
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id: {} удален администратором", commentId);
    }

    private User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
    }

    private Event validateAndGetPublishedEvent(long eventId) {
        return eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Опубликованное событие с id: %d не найдено", eventId)));
    }

    private Comment validateAndGetComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id: %d не найден", commentId)));
    }
}