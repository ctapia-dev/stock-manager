package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.CategoryRequest;
import com.cony.stockmanager.dto.response.CategoryResponse;
import com.cony.stockmanager.entity.Category;
import com.cony.stockmanager.repository.CategoryRepository;
import com.cony.stockmanager.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest request;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electrónica")
                .description("Productos electrónicos")
                .active(true)
                .build();

        request = new CategoryRequest();
        request.setName("Electrónica");
        request.setDescription("Productos electrónicos");
    }

    @Test
    void create_WhenNameNotExists_ShouldReturnCategoryResponse() {
        when(categoryRepository.existsByName("Electrónica")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        assertEquals("Electrónica", response.getName());
        assertEquals(true, response.getActive());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_WhenNameExists_ShouldThrowException() {
        when(categoryRepository.existsByName("Electrónica")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categoryService.create(request));

        assertEquals("Ya existe una categoría con ese nombre", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getById_WhenExists_ShouldReturnCategoryResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electrónica", response.getName());
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getById(99L));
    }

    @Test
    void getAll_ShouldReturnListOfCategories() {
        when(categoryRepository.findAllByActiveTrue()).thenReturn(List.of(category));

        List<CategoryResponse> responses = categoryService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Electrónica", responses.get(0).getName());
    }

    @Test
    void delete_WhenExists_ShouldSetActiveToFalse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.delete(1L);

        assertFalse(category.getActive());
        verify(categoryRepository, times(1)).save(category);
    }
}