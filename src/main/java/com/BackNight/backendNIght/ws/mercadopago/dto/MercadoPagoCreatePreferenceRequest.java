package com.BackNight.backendNIght.ws.mercadopago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // <-- IMPORTANT: Make sure this is imported
import java.util.List;

@Data // Provides getters, setters, toString, equals, hashCode
@Builder // For fluent instance building
@NoArgsConstructor // No-argument constructor (needed for Jackson/Spring)
@AllArgsConstructor // Constructor with all arguments
public class MercadoPagoCreatePreferenceRequest {

    private List<ItemRequest> items;
    private ReservationDetails reservationDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest {
        private String id;
        private String title;
        private String description;
        private String pictureUrl;
        private Integer quantity;
        private String currencyId;
        private BigDecimal unitPrice; // <-- CRITICAL: Must be BigDecimal
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationDetails {
        private Integer eventId;
        private Integer userId;
        private List<TicketDetail> tickets;
        private BigDecimal totalAmount; // <-- CRITICAL: Must be BigDecimal
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketDetail {
        private String ticketType;
        private Integer quantity;
        private BigDecimal price; // Good practice to use BigDecimal for monetary values
    }
}