package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(RequestCategoryDto requestCategoryDto);

    CategoryDto updateCategory(long catId, RequestCategoryDto requestCategoryDto);

    void deleteCategory(long catId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(long catId);
}