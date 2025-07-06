package com.BackNight.backendNIght.ws.mercadopago.dto;

public class MercadoPagoPreferenceResponse {
    private String checkoutUrl;

    public MercadoPagoPreferenceResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }
}