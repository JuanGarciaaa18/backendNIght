package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "discoteca") // Asegúrate de que el nombre de la tabla sea 'discoteca'
public class Discoteca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nit; // NIT como clave primaria

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String restricciones;

    private String ubicacion;
    private Integer capacidad;
    private String horario;

    @Column(columnDefinition = "LONGTEXT") // Asegúrate de que 'imagen' sea LONGTEXT en DB
    private String imagen;

    // Relación Many-to-One con Administradores
    @ManyToOne(fetch = FetchType.LAZY) // Mantenemos LAZY
    @JoinColumn(name = "id_admin", nullable = false) // Columna FK en la tabla 'discoteca'
    @JsonBackReference("administrador-discotecas") // Parte "hija" de la relación bidireccional con Administradores
    private Administradores administrador;

    // Relación One-to-Many con Eventos
    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-eventos") // Lado "padre" de la relación con Eventos
    private List<Evento> eventos;

    // Relación One-to-Many con Zonas
    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-zonas") // Lado "padre" de la relación con Zonas
    private List<Zona> zonas;

    // --- Constructor vacío ---
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }
}