package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.SupplierRequest;
import com.cony.stockmanager.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    SupplierResponse getById(Long id);
    List<SupplierResponse> getAll();
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}