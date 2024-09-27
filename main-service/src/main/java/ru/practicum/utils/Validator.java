package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import ru.practicum.user.repository.UserRepository;

@RequiredArgsConstructor
public class Validator {
    private final UserRepository userRepository;
}