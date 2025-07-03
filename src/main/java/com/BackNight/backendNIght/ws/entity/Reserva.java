package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference; // Importa esta
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY) // CAMBIO: A LAZY
    @JoinColumn(name = "id_cliente")
    @JsonBackReference("cliente-reservas") // AÑADIDO: Rompe el ciclo en la serialización
    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ELIMINAR: No es necesario con LAZY y @JsonBackReference
    private Clientes cliente;

    @ManyToOne(fetch = FetchType.LAZY) // CAMBIO: A LAZY
    @JoinColumn(name = "id_evento", nullable = false)
    @JsonBackReference("evento-reservas") // AÑADIDO: Rompe el ciclo en la serialización
    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ELIMINAR
    private Evento evento;

    private Integer cantidadTickets;
    private Double montoTotal;
    private String idTransaccion;
    private String preferenceId;
    private LocalDate fechaReserva;
    private String estado;
    private String estadoPago;

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
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

    public Integer getCantidadTickets() {
        return cantidadTickets;
    }

    public void setCantidadTickets(Integer cantidadTickets) {
        this.cantidadTickets = cantidadTickets;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
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
}