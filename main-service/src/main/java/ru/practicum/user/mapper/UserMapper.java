package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserRequestDto userRequestDto);

    List<UserDto> toListUserDto(List<User> users);
}