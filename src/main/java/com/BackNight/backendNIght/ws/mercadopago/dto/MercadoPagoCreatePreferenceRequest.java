package com.BackNight.backendNIght.ws.mercadopago.dto;// ws/mercadopago/dto/MercadoPagoCreatePreferenceRequest.java
import java.util.List;

public class MercadoPagoCreatePreferenceRequest {
    private List<MercadoPagoItemRequest> items;
    private Double total; // Aunque el total se recalcule en el backend, es bueno recibirlo para validación
    private String eventId; // Si necesitas asociar el pago a un evento específico


    public MercadoPagoCreatePreferenceRequest(List<MercadoPagoItemRequest> items, Double total, String eventId) {
        this.items = items;
        this.total = total;
        this.eventId = eventId;
    }

    public List<MercadoPagoItemRequest> getItems() {
        return items;
    }

    public void setItems(List<MercadoPagoItemRequest> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}