package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid RequestCategoryDto requestCategoryDto) {
        Category category = categoryMapper.toCategory(requestCategoryDto);
        return categoryMapper.toCategoryDto(categoryService.addCategory(category));
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto updateCategory(@PathVariable long catId,
                                      @RequestBody @Valid RequestCategoryDto requestCategoryDto) {
        return categoryMapper.toCategoryDto(
                categoryService.updateCategory(catId, categoryMapper.toCategory(requestCategoryDto)));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
    }
}