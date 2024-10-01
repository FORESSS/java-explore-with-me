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
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RestrictionsViolationException;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto add(RequestCategoryDto requestCategoryDto) {
        Category category = categoryMapper.toCategory(requestCategoryDto);
        categoryRepository.findCategoriesByNameContainingIgnoreCase(category.getName().toLowerCase()).ifPresent(c -> {
            throw new RestrictionsViolationException(String.format("Категория с названием: %s уже существует", category.getName()));
        });
        categoryRepository.save(category);
        log.info("Категория с id: {} создана", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(long catId, RequestCategoryDto requestCategoryDto) {
        Category updateCategory = validateAndGetCategory(catId);
        categoryRepository.findCategoriesByNameContainingIgnoreCase(
                requestCategoryDto.getName().toLowerCase()).ifPresent(c -> {
            if (c.getId() != catId) {
                throw new RestrictionsViolationException(String.format("Категория с названием: %s уже существует", requestCategoryDto.getName()));
            }
        });
        updateCategory.setName(requestCategoryDto.getName());
        log.info("Категория с id: {} обновлена", catId);
        return categoryMapper.toCategoryDto(updateCategory);
    }

    @Override
    @Transactional
    public void delete(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id: %d не найдена", catId));
        }
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new RestrictionsViolationException(String.format("Категория c id: %d уже существует", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория с id: {} удалена", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> find(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        Page<Category> pageCategories = categoryRepository.findAll(pageRequest);
        List<Category> categories;
        if (pageCategories.hasContent()) {
            categories = pageCategories.getContent();
        } else {
            categories = Collections.emptyList();
        }
        log.info("Получение списка категорий");
        return categoryMapper.toListCategoryDto(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(long catId) {
        Category category = validateAndGetCategory(catId);
        log.info("Получение категории с id: {}", catId);
        return categoryMapper.toCategoryDto(category);
    }

    private Category validateAndGetCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %d не найдена", catId)));
    }
}