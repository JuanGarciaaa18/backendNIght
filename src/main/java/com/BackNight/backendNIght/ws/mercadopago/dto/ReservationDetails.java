// src/main/java/com/BackNight/backendNIght/ws/mercadopago/dto/ReservationDetails.java

package com.BackNight.backendNIght.ws.mercadopago.dto;

import java.util.List;

public class ReservationDetails {
    private Integer eventId;
    private Integer userId;
    private List<TicketDetail> tickets;
    private Double totalAmount;

    public ReservationDetails() {
    }

    public ReservationDetails(Integer eventId, Integer userId, List<TicketDetail> tickets, Double totalAmount) {
        this.eventId = eventId;
        this.userId = userId;
        this.tickets = tickets;
        this.totalAmount = totalAmount;
    }

    // Getters y Setters
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<TicketDetail> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDetail> tickets) {
        this.tickets = tickets;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}