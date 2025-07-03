// src/main/java/com/BackNight/backendNIght/ws/mercadopago/dto/MercadoPagoCreatePreferenceRequest.java

package com.BackNight.backendNIght.ws.mercadopago.dto;

import java.util.List;

public class MercadoPagoCreatePreferenceRequest {
    private List<MercadoPagoItem> items;
    private Double total;
    private ReservationDetails reservationDetails;

    public MercadoPagoCreatePreferenceRequest() {
    }

    public MercadoPagoCreatePreferenceRequest(List<MercadoPagoItem> items, Double total, ReservationDetails reservationDetails) {
        this.items = items;
        this.total = total;
        this.reservationDetails = reservationDetails;
    }

    // Getters y Setters
    public List<MercadoPagoItem> getItems() {
        return items;
    }

    public void setItems(List<MercadoPagoItem> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public ReservationDetails getReservationDetails() {
        return reservationDetails;
    }

    public void setReservationDetails(ReservationDetails reservationDetails) {
        this.reservationDetails = reservationDetails;
    }
}