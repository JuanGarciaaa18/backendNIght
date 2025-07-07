package com.BackNight.backendNIght.ws.dto;

import com.BackNight.backendNIght.ws.entity.Reserva;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter; // Para formatear la fecha

public class ReservaDTO {
    private Integer idReserva;
    private String fechaReserva; // Formateada como String (LocalDate)
    private String estado;
    private String estadoPago;
    private Integer cantidadTickets;
    private String idTransaccion;
    private BigDecimal montoTotal;
    private String preferenceId;
    private String nombreEvento; // Nombre del evento asociado
    private Integer idEvento; // ID del evento asociado

    // --- CAMPOS DE CLIENTE AÑADIDOS ---
    private String nombreCliente;
    private String usuarioCliente;


    public ReservaDTO(Reserva reserva) {
        this.idReserva = reserva.getIdReserva();
        // Formatear la fecha para que sea legible en el frontend
        if (reserva.getFechaReserva() != null) {
            this.fechaReserva = reserva.getFechaReserva().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.fechaReserva = "N/A";
        }
        this.estado = reserva.getEstado();
        this.estadoPago = reserva.getEstadoPago();
        this.cantidadTickets = reserva.getCantidadTickets();
        this.idTransaccion = reserva.getIdTransaccion();
        this.montoTotal = reserva.getMontoTotal();
        this.preferenceId = reserva.getPreferenceId();

        // Asegúrate de que el evento no sea nulo antes de acceder a sus propiedades
        if (reserva.getEvento() != null) {
            this.nombreEvento = reserva.getEvento().getNombreEvento();
            this.idEvento = reserva.getEvento().getIdEvento();
        } else {
            this.nombreEvento = "Evento Desconocido";
            this.idEvento = null;
        }

        // --- Mapear la información del Cliente ---
        if (reserva.getCliente() != null) {
            this.nombreCliente = reserva.getCliente().getNombre();
            this.usuarioCliente = reserva.getCliente().getUsuarioCliente();
        } else {
            this.nombreCliente = "N/A"; // O un valor por defecto si el cliente es nulo
            this.usuarioCliente = "N/A";
        }
    }

    // --- Getters (asegúrate de que todos los getters existan para los nuevos campos) ---
    public Integer getIdReserva() { return idReserva; }
    public String getFechaReserva() { return fechaReserva; }
    public String getEstado() { return estado; }
    public String getEstadoPago() { return estadoPago; }
    public Integer getCantidadTickets() { return cantidadTickets; }
    public String getIdTransaccion() { return idTransaccion; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public String getPreferenceId() { return preferenceId; }
    public String getNombreEvento() { return nombreEvento; }
    public Integer getIdEvento() { return idEvento; }
    public String getNombreCliente() { return nombreCliente; } // Nuevo getter
    public String getUsuarioCliente() { return usuarioCliente; } // Nuevo getter
}
