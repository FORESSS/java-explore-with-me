package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class Validator {
    private final UserRepository userRepository;
}