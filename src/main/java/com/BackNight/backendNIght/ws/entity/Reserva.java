// src/main/java/com/BackNight/backendNIght/ws/entity/Reserva.java
package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Clientes cliente;

    private String fechaReserva;
    private String estado;
    private String estadoPago; // ¡NUEVO CAMPO!

    // --- Getters y Setters ---

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

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() { // ¡NUEVO GETTER!
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) { // ¡NUEVO SETTER!
        this.estadoPago = estadoPago;
    }
}