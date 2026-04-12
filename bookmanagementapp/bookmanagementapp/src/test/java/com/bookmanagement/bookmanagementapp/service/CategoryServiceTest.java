package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.CategoryRequest;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.repository.CategoryRepository;
import com.bookmanagement.bookmanagementapp.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, new com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper());
    }

    @Test
    void createCategoryShouldRejectDuplicateName() {
        when(categoryRepository.existsByNameIgnoreCase("Finance")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(new CategoryRequest("Finance")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Category already exists");
    }

    @Test
    void createCategoryShouldTrimName() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(6L);
            return category;
        });

        var response = categoryService.createCategory(new CategoryRequest("  Finance  "));

        assertThat(response.id()).isEqualTo(6L);
        assertThat(response.name()).isEqualTo("Finance");
    }
}
