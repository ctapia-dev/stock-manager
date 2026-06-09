package com.cony.stockmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAlertDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Integer minStock;
}