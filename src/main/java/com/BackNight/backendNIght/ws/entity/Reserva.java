package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.sql.Date; // O java.time.LocalDate si prefieres API moderna

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @Column(name = "fecha_reserva")
    private Date fechaReserva; // Considera usar java.time.LocalDate

    private String estado; // RESERVADA, CONFIRMADA, CANCELADA, etc.

    @Column(name = "estado_pago")
    private String estadoPago; // PENDIENTE, PAGADO, FALLIDO, etc.

    @Column(name = "cantidad_tickets")
    private Integer cantidadTickets;

    @Column(name = "id_transaccion")
    private String idTransaccion;

    @Column(name = "monto_total", precision = 10, scale = 2)
    private Double montoTotal;

    @Column(name = "preference_id")
    private String preferenceId;

    // Relación Many-to-One con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    @JsonBackReference("cliente-reservas") // Parte "hija" de la relación bidireccional
    private Clientes cliente;

    // Relación Many-to-One con Evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento") // Columna FK en la tabla 'reservas'
    @JsonBackReference("evento-reservas") // Parte "hija" de la relación bidireccional
    private Evento evento;

    // --- Constructor vacío ---
    public Reserva() {
    }

    // --- Getters y Setters ---

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Integer getCantidadTickets() {
        return cantidadTickets;
    }

    public void setCantidadTickets(Integer cantidadTickets) {
        this.cantidadTickets = cantidadTickets;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}