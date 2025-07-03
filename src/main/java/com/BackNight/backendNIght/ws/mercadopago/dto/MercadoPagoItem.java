// src/main/java/com/BackNight/backendNIght/ws/mercadopago/dto/MercadoPagoItem.java

package com.BackNight.backendNIght.ws.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // ¡Importante para snake_case!

public class MercadoPagoItem {
    private String id;
    private String title;
    private String description;

    @JsonProperty("picture_url") // Mapea 'picture_url' del JSON a 'pictureUrl' en Java
    private String pictureUrl;

    private Integer quantity;

    @JsonProperty("unit_price") // Mapea 'unit_price' del JSON a 'unitPrice' en Java
    private Double unitPrice;

    @JsonProperty("currency_id") // Mapea 'currency_id' del JSON a 'currencyId' en Java
    private String currencyId;

    public MercadoPagoItem() {
    }

    public MercadoPagoItem(String id, String title, String description, String pictureUrl, Integer quantity, Double unitPrice, String currencyId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currencyId = currencyId;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrencyId() { return currencyId; }
    public void setCurrencyId(String currencyId) { this.currencyId = currencyId; }
}