package com.BackNight.backendNIght.ws.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Eventos {

    @Id
    private String id_event;
    private String nombreEvent;
    private String capacidadEvento;
    private Integer id_servicio;
    private Integer id_reserva;
    private Integer id_admin;

    public String getId_event() {
        return id_event;
    }

    public void setId_event(String id_event) {
        this.id_event = id_event;
    }

    public String getNombreEvent() {
        return nombreEvent;
    }

    public void setNombreEvent(String nombreEvent) {
        this.nombreEvent = nombreEvent;
    }

    public String getCapacidadEvento() {
        return capacidadEvento;
    }

    public void setCapacidadEvento(String capacidadEvento) {
        this.capacidadEvento = capacidadEvento;
    }

    public Integer getId_servicio() {
        return id_servicio;
    }

    public void setId_servicio(Integer id_servicio) {
        this.id_servicio = id_servicio;
    }

    public Integer getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(Integer id_reserva) {
        this.id_reserva = id_reserva;
    }

    public Integer getId_admin() {
        return id_admin;
    }

    public void setId_admin(Integer id_admin) {
        this.id_admin = id_admin;
    }
}