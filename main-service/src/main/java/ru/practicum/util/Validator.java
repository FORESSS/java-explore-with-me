package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.IntegrityViolationException;
import ru.practicum.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class Validator {
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public Compilation validateAndGetCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id: %d не найдена", compId)));
    }

    public Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }

    public void checkNewCategory(NewCategoryDto newCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(newCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            throw new IntegrityViolationException(String.format("Категория %s уже создана", newCategoryDto.getName()));
        });
    }

    public void checkCategory(UpdateCategoryDto updateCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(updateCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            throw new IntegrityViolationException(String.format("Категория %s уже создана", updateCategoryDto.getName()));
        });
    }

    public void checkCategory(long catId) {
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new IntegrityViolationException("Категория уже создана");
        }
    }
}