package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.utils.Validator;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final Validator validator;

    @Override
    @Transactional
    public CategoryDto addCategory(RequestCategoryDto requestCategoryDto) {
        validator.checkNewCategory(requestCategoryDto);
        Category category = categoryMapper.toCategory(requestCategoryDto);
        categoryRepository.save(category);
        log.info("Категория с id: {} создана", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, RequestCategoryDto requestCategoryDto) {
        validator.checkNewCategory(requestCategoryDto);
        Category updateCategory = validator.validateAndGetCategory(catId);
        Category category = categoryMapper.toCategory(requestCategoryDto);
        updateCategory.setName(category.getName());
        log.info("Категория с id: {} обновлена", catId);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        validator.checkCategoryId(catId);
        validator.checkCategory(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория с id: {} удалена", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        Page<Category> pageCategories = categoryRepository.findAll(pageRequest);
        List<Category> categories;
        if (pageCategories.hasContent()) {
            categories = pageCategories.getContent();
        } else {
            categories = Collections.emptyList();
        }
        log.info("Получение списка всех категорий");
        return categoryMapper.toListCategoryDto(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        Category category = validator.validateAndGetCategory(catId);
        log.info("Получение категории с id: {}", catId);
        return categoryMapper.toCategoryDto(category);
    }
}