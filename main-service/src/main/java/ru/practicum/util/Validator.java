package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class Validator {
    private final CompilationRepository compilationRepository;

    public Compilation validateAndGetCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id: %d не найдена", compId)));
    }
}