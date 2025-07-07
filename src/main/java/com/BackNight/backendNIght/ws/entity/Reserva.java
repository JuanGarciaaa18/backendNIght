package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    // --- RELACIÓN CRÍTICA CON CLIENTE ---
    @ManyToOne(fetch = FetchType.LAZY) // Muchas reservas pueden ser de un cliente
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", nullable = false) // Columna FK en 'reservas'
    @JsonBackReference("cliente-reservas") // Evita recursión en JSON si Cliente tiene una lista de Reservas
    private Clientes cliente; // Asegúrate de que esta sea la clase correcta (Clientes o Cliente)

    // Relación con Evento
    @ManyToOne(fetch = FetchType.LAZY) // Muchas reservas pueden ser para un evento
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento", nullable = false) // Columna FK en 'reservas'
    @JsonBackReference("evento-reservas") // Evita recursión en JSON si Evento tiene una lista de Reservas
    private Evento evento;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "estado")
    private String estado;

    @Column(name = "estado_pago")
    private String estadoPago;

    @Column(name = "cantidad_tickets")
    private Integer cantidadTickets;

    @Column(name = "id_transaccion")
    private String idTransaccion; // ID de transacción de Mercado Pago

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "preference_id")
    private String preferenceId; // ID de preferencia de Mercado Pago

    // Constructor por defecto
    public Reserva() {}

    // Getters y Setters
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

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) { // <-- TYPO CORREGIDO AQUÍ
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

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }
}
