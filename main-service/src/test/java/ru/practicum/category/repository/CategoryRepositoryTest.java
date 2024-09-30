package ru.practicum.category.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.category.model.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveCategoryTest() {
        Category category = new Category();
        category.setName("Category");
        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getId());
        assertEquals(category.getName(), savedCategory.getName());
    }

    @Test
    void findCategoryByNameTest() {
        Category category = new Category();
        category.setName("Category");
        categoryRepository.save(category);
        Optional<Category> foundCategory = categoryRepository.findCategoriesByNameContainingIgnoreCase("Category");

        assertNotNull(foundCategory);
        assertEquals(category.getName(), foundCategory.get().getName());
    }

    @Test
    void findAllCategoriesTest() {
        Category category1 = new Category();
        category1.setName("Category 1");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Category 2");
        categoryRepository.save(category2);
        List<Category> categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }
}