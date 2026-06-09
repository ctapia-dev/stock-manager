package com.cony.stockmanager.service.impl;

import com.cony.stockmanager.dto.request.StockMovementRequest;
import com.cony.stockmanager.dto.response.StockAlertDTO;
import com.cony.stockmanager.dto.response.StockMovementResponse;
import com.cony.stockmanager.entity.Product;
import com.cony.stockmanager.entity.StockMovement;
import com.cony.stockmanager.entity.User;
import com.cony.stockmanager.repository.ProductRepository;
import com.cony.stockmanager.repository.StockMovementRepository;
import com.cony.stockmanager.repository.UserRepository;
import com.cony.stockmanager.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public StockMovementResponse register(StockMovementRequest request, String username) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar stock según tipo de movimiento
        switch (request.getType()) {
            case IN -> product.setStock(product.getStock() + request.getQuantity());
            case OUT -> {
                if (product.getStock() < request.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente. Stock actual: " + product.getStock());
                }
                product.setStock(product.getStock() - request.getQuantity());
            }
            case ADJUSTMENT -> product.setStock(request.getQuantity());
        }

        productRepository.save(product);

        // Guardar movimiento
        StockMovement movement = StockMovement.builder()
                .product(product)
                .user(user)
                .type(request.getType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .build();

        StockMovement saved = stockMovementRepository.save(movement);

        // Alerta WebSocket si stock bajo
        if (product.getStock() <= product.getMinStock()) {
            messagingTemplate.convertAndSend("/topic/stock-alerts",
                    new StockAlertDTO(
                            product.getId(),
                            product.getName(),
                            product.getSku(),
                            product.getStock(),
                            product.getMinStock()
                    ));
        }

        return toResponse(saved, product.getStock());
    }

    @Override
    public Page<StockMovementResponse> getByProduct(Long productId, Pageable pageable) {
        return stockMovementRepository.findAllByProductId(productId, pageable)
                .map(m -> toResponse(m, m.getProduct().getStock()));
    }

    @Override
    public Page<StockMovementResponse> getAll(Pageable pageable) {
        return stockMovementRepository.findAll(pageable)
                .map(m -> toResponse(m, m.getProduct().getStock()));
    }

    private StockMovementResponse toResponse(StockMovement movement, Integer stockAfter) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productName(movement.getProduct().getName())
                .productSku(movement.getProduct().getSku())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .stockAfter(stockAfter)
                .reason(movement.getReason())
                .username(movement.getUser().getUsername())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}