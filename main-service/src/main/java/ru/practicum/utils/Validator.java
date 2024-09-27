package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.repository.UserRepository;

@RequiredArgsConstructor
public class Validator {
    private final UserRepository userRepository;

    public void checkUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", userId));
        }
    }
}