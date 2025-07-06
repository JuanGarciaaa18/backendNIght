package com.BackNight.backendNIght.ws.mercadopago.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReservationDetails {
    private Integer eventId;
    private Integer userId;
    private List<TicketDetail> tickets;
    private BigDecimal totalAmount;

    public ReservationDetails() {}

    public ReservationDetails(Integer eventId, Integer userId, List<TicketDetail> tickets, BigDecimal totalAmount) {
        this.eventId = eventId;
        this.userId = userId;
        this.tickets = tickets;
        this.totalAmount = totalAmount;
    }

    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<TicketDetail> getTickets() { return tickets; }
    public void setTickets(List<TicketDetail> tickets) { this.tickets = tickets; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}