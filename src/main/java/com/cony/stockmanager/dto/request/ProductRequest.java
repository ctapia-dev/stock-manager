package com.cony.stockmanager.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50)
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minStock;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El proveedor es obligatorio")
    private Long supplierId;
}