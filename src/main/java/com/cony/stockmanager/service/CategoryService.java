package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.CategoryRequest;
import com.cony.stockmanager.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}