package com.eatcarefully.backend.services;

import com.eatcarefully.backend.model.Category;
import com.eatcarefully.backend.repository.CategoryRepository;
import com.eatcarefully.backend.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {


    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_ReturnExistingCategory_When_Found() {

        String name = "existingCat";
        Category category = new Category();
        category.setName(name);
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(category));

        Category result = categoryService.findOrCreateCategory(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(categoryRepository, times(1)).findByName(name);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void Should_CreateNewCategory_When_NotFound() {

        String name = "newCategory";
        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());
        Category newCategory = new Category(null, name);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        Category result = categoryService.findOrCreateCategory(name);


        assertNotNull(result);
        verify(categoryRepository, times(1)).findByName(name);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }



}
