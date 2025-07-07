package com.BackNight.backendNIght.ws.mercadopago.dto;

import java.math.BigDecimal;
import java.util.List;

public class MercadoPagoCreatePreferenceRequest {
    private List<Item> items;
    private BigDecimal total;
    private ReservationDetails reservationDetails;

    // Getters y Setters
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ReservationDetails getReservationDetails() {
        return reservationDetails;
    }

    public void setReservationDetails(ReservationDetails reservationDetails) {
        this.reservationDetails = reservationDetails;
    }

    // Clase interna para los ítems
    public static class Item {
        private String id;
        private String title;
        private String description;
        private String pictureUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String currencyId;

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
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public String getCurrencyId() { return currencyId; }
        public void setCurrencyId(String currencyId) { this.currencyId = currencyId; }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    '}';
        }
    }

    // Clase interna para los detalles de la reserva
    public static class ReservationDetails {
        private Integer eventId;
        private Integer userId; // <-- ¡Este campo es CRÍTICO!
        private List<Ticket> tickets;
        private BigDecimal totalAmount;

        // Getters y Setters
        public Integer getEventId() { return eventId; }
        public void setEventId(Integer eventId) { this.eventId = eventId; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public List<Ticket> getTickets() { return tickets; }
        public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        @Override
        public String toString() {
            return "ReservationDetails{" +
                    "eventId=" + eventId +
                    ", userId=" + userId +
                    ", totalAmount=" + totalAmount +
                    '}';
        }
    }

    // Clase interna para los tickets dentro de ReservationDetails
    public static class Ticket {
        private Integer zonaId;
        private Integer quantity;
        private BigDecimal unitPrice;

        // Getters y Setters
        public Integer getZonaId() { return zonaId; }
        public void setZonaId(Integer zonaId) { this.zonaId = zonaId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        @Override
        public String toString() {
            return "Ticket{" +
                    "zonaId=" + zonaId +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MercadoPagoCreatePreferenceRequest{" +
                "items=" + items +
                ", total=" + total +
                ", reservationDetails=" + reservationDetails +
                '}';
    }
}
