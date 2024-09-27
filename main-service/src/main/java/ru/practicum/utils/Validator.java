package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.IntegrityViolationException;
import ru.practicum.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class Validator {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public void checkCategoryId(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id: %d не найдена", catId));
        }
    }

    public Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }

    public void checkNewCategory(RequestCategoryDto requestCategoryDto) {
        categoryRepository.findCategoriesByNameContainingIgnoreCase(requestCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            throw new IntegrityViolationException(String.format("Категория %s уже существует", requestCategoryDto.getName()));
        });
    }

    public void checkCategory(long catId) {
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new IntegrityViolationException(String.format("Категория c id: %d уже существует", catId));
        }
    }
}