package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class Validator {
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private RequestsRepository requestsRepository;
    private CategoryRepository categoryRepository;
    private CompilationRepository compilationRepository;
}