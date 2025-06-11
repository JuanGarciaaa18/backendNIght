package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "discoteca") // << Asegúrate que coincida con el nombre en la base de datos
public class Discoteca {
    @Id
    private Integer nit;

    private String nombre;
    private String restricciones;
    private String ubicacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    private String horario;

    @Column(name = "id_admin")
    private Integer idAdmin;

    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    @OneToMany(mappedBy = "discoteca", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Zona> zonas;

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

    public Integer getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin;
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