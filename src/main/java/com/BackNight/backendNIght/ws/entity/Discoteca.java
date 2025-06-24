package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "discoteca")
public class Discoteca {
    @Id
    @Column(name = "nit") // Explicitly map to 'nit' column
    private Integer nit;

    private String nombre;
    private String restricciones;
    private String ubicacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    private String horario;

    // Cambiado de Integer idAdmin a una relación Many-to-One con Administradores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin") // Columna de la clave foránea en la tabla 'discoteca'
    private Administradores administrador;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Zona> zonas;

    // --- Getters y Setters ---

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    // Ya no necesitas getIdAdmin/setIdAdmin directamente, ahora es via getAdministrador/setAdministrador
    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNit() {
        return nit;
    }

    public void setNit(Integer nit) {
        this.nit = nit;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(String restricciones) {
        this.restricciones = restricciones;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }
}
