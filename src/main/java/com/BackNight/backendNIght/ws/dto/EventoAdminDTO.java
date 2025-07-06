package com.BackNight.backendNIght.ws.dto;

import com.BackNight.backendNIght.ws.entity.Evento;
import java.math.BigDecimal;

public class EventoAdminDTO {
    private Integer idEvento;
    private String nombreEvento;
    private String descripcion;
    private String fecha;
    private String hora;
    private BigDecimal precio;
    private String imagen;
    private DiscotecaInfoDTO discoteca; // DTO anidado

    public EventoAdminDTO() {}

    public EventoAdminDTO(Evento evento) {
        this.idEvento = evento.getIdEvento();
        this.nombreEvento = evento.getNombreEvento();
        this.descripcion = evento.getDescripcion();
        this.fecha = evento.getFecha();
        this.hora = evento.getHora();
        this.precio = evento.getPrecio();
        this.imagen = evento.getImagen();
        // Asegúrate de que el objeto discoteca esté cargado antes de intentar acceder a él
        if (evento.getDiscoteca() != null) {
            this.discoteca = new DiscotecaInfoDTO(evento.getDiscoteca().getNit(), evento.getDiscoteca().getNombre());
        }
        // No necesitas manejar la referencia de Administrador aquí si no la envías en el DTO
    }

    // Getters y Setters (ya los tienes)
    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public DiscotecaInfoDTO getDiscoteca() { return discoteca; }
    public void setDiscoteca(DiscotecaInfoDTO discoteca) { this.discoteca = discoteca; }
}