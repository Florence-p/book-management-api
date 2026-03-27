package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.CategoryRequest;
import com.bookmanagement.bookmanagementapp.dto.CategoryResponse;
import com.bookmanagement.bookmanagementapp.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

}
