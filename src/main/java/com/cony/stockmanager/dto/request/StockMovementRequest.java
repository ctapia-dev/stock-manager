package com.cony.stockmanager.dto.request;

import com.cony.stockmanager.entity.StockMovement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockMovementRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private StockMovement.MovementType type;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @NotBlank(message = "El motivo es obligatorio")
    private String reason;
}