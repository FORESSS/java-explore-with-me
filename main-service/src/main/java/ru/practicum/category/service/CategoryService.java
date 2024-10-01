package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(RequestCategoryDto requestCategoryDto);

    CategoryDto update(long catId, RequestCategoryDto requestCategoryDto);

    void delete(long catId);

    List<CategoryDto> find(int from, int size);

    CategoryDto findById(long catId);
}