package com.cony.stockmanager.service;

import com.cony.stockmanager.dto.request.StockMovementRequest;
import com.cony.stockmanager.dto.response.StockMovementResponse;
import com.cony.stockmanager.entity.*;
import com.cony.stockmanager.repository.ProductRepository;
import com.cony.stockmanager.repository.StockMovementRepository;
import com.cony.stockmanager.repository.UserRepository;
import com.cony.stockmanager.service.impl.StockMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private Product product;
    private User user;
    private StockMovementRequest request;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Samsung Galaxy S24")
                .sku("SAM-S24-001")
                .stock(10)
                .minStock(3)
                .price(new BigDecimal("899990"))
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("cony")
                .email("cony@test.com")
                .password("hashedpassword")
                .role(User.Role.ADMIN)
                .active(true)
                .build();

        request = new StockMovementRequest();
        request.setProductId(1L);
        request.setQuantity(5);
        request.setReason("Test movimiento");
    }

    @Test
    void register_IN_ShouldIncreaseStock() {
        request.setType(StockMovement.MovementType.IN);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("cony")).thenReturn(Optional.of(user));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setProduct(product);
            m.setUser(user);
            return m;
        });

        StockMovementResponse response = stockMovementService.register(request, "cony");

        assertEquals(15, product.getStock());
        assertEquals(StockMovement.MovementType.IN, response.getType());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void register_OUT_ShouldDecreaseStock() {
        request.setType(StockMovement.MovementType.OUT);
        request.setQuantity(3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("cony")).thenReturn(Optional.of(user));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setProduct(product);
            m.setUser(user);
            return m;
        });

        stockMovementService.register(request, "cony");

        assertEquals(7, product.getStock());
    }

    @Test
    void register_OUT_WhenInsufficientStock_ShouldThrowException() {
        request.setType(StockMovement.MovementType.OUT);
        request.setQuantity(99);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("cony")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stockMovementService.register(request, "cony"));

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        assertEquals(10, product.getStock());
    }

    @Test
    void register_OUT_WhenStockBelowMinimum_ShouldSendAlert() {
        request.setType(StockMovement.MovementType.OUT);
        request.setQuantity(9);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("cony")).thenReturn(Optional.of(user));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setProduct(product);
            m.setUser(user);
            return m;
        });

        stockMovementService.register(request, "cony");

        assertEquals(1, product.getStock());
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/stock-alerts"), any(Object.class));
    }

    @Test
    void register_ADJUSTMENT_ShouldSetExactStock() {
        request.setType(StockMovement.MovementType.ADJUSTMENT);
        request.setQuantity(50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("cony")).thenReturn(Optional.of(user));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setProduct(product);
            m.setUser(user);
            return m;
        });

        stockMovementService.register(request, "cony");

        assertEquals(50, product.getStock());
    }
}