package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CategoryMapper {
    Category toCategory(RequestCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toListCategoryDto(List<Category> categoryList);
}