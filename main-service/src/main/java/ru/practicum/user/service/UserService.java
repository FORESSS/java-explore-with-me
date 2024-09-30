package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    List<UserDto> find(List<Long> ids, int from, int size);

    UserDto add(UserRequestDto userRequestDto);

    void delete(long userId);
}