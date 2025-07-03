// src/main/java/com/BackNight/backendNIght/ws/dto/ReservaDTO.java
package com.BackNight.backendNIght.ws.dto;

import java.time.LocalDate;

public class ReservaDTO {
    private Integer idReserva;
    private Integer idEvento;
    private String nombreEvento;
    private String nombreCliente; // Cambiado de nombreUsuarioCliente para consistencia con la entidad Cliente
    private Integer cantidadTickets;
    private Double montoTotal; // AÑADIDO
    private String idTransaccion;
    private String preferenceId; // AÑADIDO
    private LocalDate fechaReserva;
    private String estado; // AÑADIDO
    private String estadoPago;


    // Constructor completo para mapear desde la entidad Reserva
    public ReservaDTO(Integer idReserva, Integer idEvento, String nombreEvento, String nombreCliente,
                      Integer cantidadTickets, Double montoTotal, String idTransaccion,
                      String preferenceId, LocalDate fechaReserva, String estado, String estadoPago) { // Constructor ACTUALIZADO
        this.idReserva = idReserva;
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.nombreCliente = nombreCliente;
        this.cantidadTickets = cantidadTickets;
        this.montoTotal = montoTotal; // Asignado
        this.idTransaccion = idTransaccion;
        this.preferenceId = preferenceId; // Asignado
        this.fechaReserva = fechaReserva;
        this.estado = estado; // Asignado
        this.estadoPago = estadoPago;
    }

    // Constructor vacío (necesario si vas a deserializar JSON a este DTO)
    public ReservaDTO() {
    }

    // --- Getters y Setters (Asegúrate de que los getters y setters para los campos añadidos también existan) ---
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    public String getNombreCliente() { return nombreCliente; } // Getter actualizado
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; } // Setter actualizado
    public Integer getCantidadTickets() { return cantidadTickets; }
    public void setCantidadTickets(Integer cantidadTickets) { this.cantidadTickets = cantidadTickets; }

    // NUEVOS GETTERS Y SETTERS
    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    // FIN NUEVOS GETTERS Y SETTERS

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(String idTransaccion) { this.idTransaccion = idTransaccion; }
}