package com.BackNight.backendNIght.ws.mercadopago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty; // Importar esta clase

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

    @JsonProperty("unit_price") // ¡CAMBIO CLAVE AQUÍ!
    private BigDecimal unitPrice;
}