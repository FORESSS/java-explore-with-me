package ru.practicum.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryPublicController.class)
public class CategoryPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private ObjectMapper objectMapper;
    private CategoryDto categoryDto;

    @BeforeEach
    public void setup() {
        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");
    }

    @Test
    public void getAllCategoriesTest() throws Exception {
        List<CategoryDto> categories = Collections.singletonList(categoryDto);
        Mockito.when(categoryService.getAllCategories(any(Integer.class), any(Integer.class)))
                .thenReturn(categories);
        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Category")));

        Mockito.verify(categoryService).getAllCategories(any(Integer.class), any(Integer.class));
    }

    @Test
    public void getCategoryByIdTest() throws Exception {
        Mockito.when(categoryService.getCategoryById(1L))
                .thenReturn(categoryDto);
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Category")));

        Mockito.verify(categoryService).getCategoryById(1L);
    }
}