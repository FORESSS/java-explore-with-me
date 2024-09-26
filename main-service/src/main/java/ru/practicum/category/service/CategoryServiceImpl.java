package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.util.Validator;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final Validator validator;

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
        log.info("Получение всех категорий");
        return categoryMapper.toListCategoryDto(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        Category category = validator.validateAndGetCategory(catId);
        log.info("Получение категории с id: {}", catId);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        validator.checkNewCategory(newCategoryDto);
        Category createCategory = categoryMapper.toCategory(newCategoryDto);
        categoryRepository.save(createCategory);
        log.info("Категория с id: {} создана", createCategory.getId());
        return categoryMapper.toCategoryDto(createCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, UpdateCategoryDto updateCategoryDto) {
        Category updateCategory = validator.validateAndGetCategory(catId);
        validator.checkCategory(updateCategoryDto);
        updateCategory.setName(updateCategory.getName());
        log.info("Категория с id: {} обновлена", catId);
        return categoryMapper.toCategoryDto(updateCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        validator.checkCategoryId(catId);
        validator.checkCategory(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория с id: {} удалена", catId);
    }
}