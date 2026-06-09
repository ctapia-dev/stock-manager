package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.ProductRequest;
import com.cony.stockmanager.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAll(Pageable pageable);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}