package com.BackNight.backendNIght.ws.mercadopago.dto;

import java.math.BigDecimal;

public class TicketDetail {
    private Integer zonaId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public TicketDetail() {
    }

    public TicketDetail(Integer zonaId, Integer quantity, BigDecimal unitPrice) {
        this.zonaId = zonaId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters y Setters
    public Integer getZonaId() {
        return zonaId;
    }

    public void setZonaId(Integer zonaId) {
        this.zonaId = zonaId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}