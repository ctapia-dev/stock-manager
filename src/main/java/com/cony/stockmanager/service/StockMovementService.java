package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.StockMovementRequest;
import com.cony.stockmanager.dto.response.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementService {
    StockMovementResponse register(StockMovementRequest request, String username);
    Page<StockMovementResponse> getByProduct(Long productId, Pageable pageable);
    Page<StockMovementResponse> getAll(Pageable pageable);
}