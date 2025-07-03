// src/main/java/com/BackNight/backendNIght/ws/mercadopago/dto/TicketDetail.java

package com.BackNight.backendNIght.ws.mercadopago.dto;

public class TicketDetail {
    private Integer zonaId; // <-- ¡Este campo es crucial y debe estar aquí!
    private Integer quantity;
    private Double unitPrice;

    public TicketDetail() {
    }

    public TicketDetail(Integer zonaId, Integer quantity, Double unitPrice) {
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

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}