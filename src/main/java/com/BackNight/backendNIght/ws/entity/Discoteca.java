package com.BackNight.backendNIght.ws.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Discoteca {

    @Id
    private String nit;
    private String nombre;
    private String ubicacion;
    private Integer capacidad; // Cambiado de int a Integer
    private String imagen;

    // Getters y setters
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}