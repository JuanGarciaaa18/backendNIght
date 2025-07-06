package com.BackNight.backendNIght.ws.mercadopago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty; // Importar esta clase

import java.math.BigDecimal;
import java.util.List; // Necesario si no está ya

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

    @JsonProperty("unit_price") // ¡Añade esta línea!
    private BigDecimal unitPrice;
}