package com.cony.stockmanager.dto.response;

import com.cony.stockmanager.entity.StockMovement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    private Long id;
    private String productName;
    private String productSku;
    private StockMovement.MovementType type;
    private Integer quantity;
    private Integer stockAfter;
    private String reason;
    private String username;
    private LocalDateTime createdAt;
}