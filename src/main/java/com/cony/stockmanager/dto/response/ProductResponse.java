package com.cony.stockmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private Boolean active;
    private String categoryName;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}