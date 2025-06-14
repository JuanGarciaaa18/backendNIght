package com.BackNight.backendNIght.ws.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvento;

    private String nombreEvento;
    private String descripcion;
    private String fecha;
    private String hora;
    private double precio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nit_discoteca")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Discoteca discoteca;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_admin")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Administradores administrador;

    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Discoteca getDiscoteca() {
        return discoteca;
    }

    public void setDiscoteca(Discoteca discoteca) {
        this.discoteca = discoteca;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Integer getIdEvento() {
        return Math.toIntExact(idEvento);
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
