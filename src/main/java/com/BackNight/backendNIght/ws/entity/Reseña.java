package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime; // Para manejar la fecha y hora de la reseña

@Entity
@Table(name = "reseñas")
public class Reseña {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReseña;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "contrasenaCliente", "usuarioCliente"}) // Ignorar campos sensibles del cliente
    private Clientes cliente; // Relacionado con la entidad Cliente

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nit_discoteca", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "administrador", "zonas"}) // Ignorar campos sensibles de discoteca
    private Discoteca discoteca; // Relacionado con la entidad Discoteca

    private Integer puntuacion; // Puntuación de 1 a 5
    private String comentario;
    private LocalDateTime fechaReseña; // Usamos LocalDateTime para fecha y hora

    // Constructor vacío (necesario para JPA)
    public Reseña() {
    }

    // --- Getters y Setters ---

    public Integer getIdReseña() {
        return idReseña;
    }

    public void setIdReseña(Integer idReseña) {
        this.idReseña = idReseña;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Discoteca getDiscoteca() {
        return discoteca;
    }

    public void setDiscoteca(Discoteca discoteca) {
        this.discoteca = discoteca;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaReseña() {
        return fechaReseña;
    }

    public void setFechaReseña(LocalDateTime fechaReseña) {
        this.fechaReseña = fechaReseña;
    }
}