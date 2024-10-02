package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CommentMapper {
    Comment toComment(NewCommentDto newCommentDto);

    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toListCommentDto(List<Comment> comments);
}