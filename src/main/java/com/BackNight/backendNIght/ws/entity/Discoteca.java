package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "discoteca")
public class Discoteca {
    @Id
    @Column(name = "nit")
    // Si tu columna 'nit' en la DB es AUTO_INCREMENT, descomenta la siguiente línea:
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nit;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "restricciones")
    private String restricciones;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "horario")
    private String horario;

    @ManyToOne(fetch = FetchType.LAZY) // Mantenemos LAZY
    @JoinColumn(name = "id_admin", nullable = false) // Columna FK en la tabla 'discoteca'
    @JsonBackReference("administrador-discotecas") // Parte "hija" de la relación bidireccional con Administradores
    private Administradores administrador;

    @Column(name = "imagen", columnDefinition = "LONGTEXT") // Mapea a LONGTEXT en DB
    private String imagen;

    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-zonas") // Parte "padre" de la relación bidireccional con Zona
    private List<Zona> zonas;

    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-eventos") // Parte "padre" de la relación bidireccional con Evento
    private List<Evento> eventos;

    // --- Constructor por defecto (necesario para JPA) ---
    public Discoteca() {
    }

    // --- Getters y Setters ---
    public Integer getNit() {
        return nit;
    }

    public void setNit(Integer nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

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

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
}