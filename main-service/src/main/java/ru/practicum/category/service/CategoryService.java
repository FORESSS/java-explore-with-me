package ru.practicum.category.service;

import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category);

    Category updateCategory(long catId, Category category);

    void deleteCategory(long catId);

    List<Category> getAllCategories(int from, int size);

    Category getCategoryById(long catId);
}