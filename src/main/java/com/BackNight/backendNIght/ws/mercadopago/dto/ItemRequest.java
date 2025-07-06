package com.BackNight.backendNIght.ws.mercadopago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private String id;
    private String title;
    private String description;
    private String pictureUrl;
    private Integer quantity;
    private String currencyId;
    private BigDecimal unitPrice; // Consistente con BigDecimal
}