package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategory(long catId);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long catId, UpdateCategoryDto updateCategoryDto);

    void deleteCategory(long catId);
}