package com.BackNight.backendNIght.ws.mercadopago.dto;

// ws/mercadopago/dto/MercadoPagoItemRequest.java
public class MercadoPagoItemRequest {
    private String id; // Opcional, si tienes IDs para tus zonas/productos
    private String title;
    private String description;
    private String pictureUrl;
    private Integer quantity;
    private Double unitPrice;
    private String currencyId; // Ej: "COP"

    public MercadoPagoItemRequest(String id, String title, String description, String pictureUrl, Integer quantity, Double unitPrice, String currencyId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currencyId = currencyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
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

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
}