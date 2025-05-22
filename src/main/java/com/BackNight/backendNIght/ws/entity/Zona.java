package com.BackNight.backendNIght.ws.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Zona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private int capacidad;
    private boolean disponible;
    private String descripcion;
    private double precioReserva;

    @ManyToOne
    @JoinColumn(name = "discoteca_id")
    private Discoteca discoteca;

    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL)
    private List<Mesa> mesas;

    // Getters y setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioReserva() {
        return precioReserva;
    }

    public void setPrecioReserva(double precioReserva) {
        this.precioReserva = precioReserva;
    }

    public Discoteca getDiscoteca() {
        return discoteca;
    }

    public void setDiscoteca(Discoteca discoteca) {
        this.discoteca = discoteca;
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(List<Mesa> mesas) {
        this.mesas = mesas;
    }
}