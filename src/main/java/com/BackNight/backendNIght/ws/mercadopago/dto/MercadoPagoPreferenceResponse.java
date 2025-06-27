package com.BackNight.backendNIght.ws.mercadopago.dto;

// ws/mercadopago/dto/MercadoPagoPreferenceResponse.java
public class MercadoPagoPreferenceResponse {
    private String checkoutUrl; // La URL a la que el frontend debe redirigir

    public MercadoPagoPreferenceResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    // Getter
    public String getCheckoutUrl() {
        return checkoutUrl;
    }
}