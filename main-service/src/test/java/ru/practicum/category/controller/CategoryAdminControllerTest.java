package ru.practicum.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.RequestCategoryDto;
import ru.practicum.category.service.CategoryService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryAdminController.class)
public class CategoryAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private ObjectMapper objectMapper;
    private RequestCategoryDto requestCategoryDto;
    private CategoryDto categoryDto;

    @BeforeEach
    public void setup() {
        requestCategoryDto = new RequestCategoryDto();
        requestCategoryDto.setName("Category");
        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category");
    }

    @Test
    public void addCategoryTest() throws Exception {
        when(categoryService.add(any(RequestCategoryDto.class)))
                .thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Category")));

        Mockito.verify(categoryService).add(any(RequestCategoryDto.class));
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(categoryService.update(anyLong(), any(RequestCategoryDto.class)))
                .thenReturn(categoryDto);

        mockMvc.perform(patch("/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Category")));

        Mockito.verify(categoryService).update(anyLong(), any(RequestCategoryDto.class));
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(categoryService).delete(anyLong());
    }
}