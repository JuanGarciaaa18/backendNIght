package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Asegúrate de importar esta
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "discoteca")
public class Discoteca {
    @Id
    @Column(name = "nit")
    private Integer nit;

    private String nombre;
    private String restricciones;
    private String ubicacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    private String horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin")
    // @JsonBackReference("administrador-discotecas") // Si Administradores tiene List<Discoteca>
    private Administradores administrador;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-zonas") // CAMBIO: Lado "padre" de la relación con Zona
    private List<Zona> zonas;

    // RELACIÓN AÑADIDA: Una discoteca puede tener muchos eventos
    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("discoteca-eventos") // AÑADIDO: Lado "padre" de la relación con Evento
    private List<Evento> eventos; // Asegúrate de tener esta lista

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