package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;

    @ManyToOne(fetch = FetchType.LAZY) // Mantenemos LAZY
    @JoinColumn(name = "nit_discoteca", nullable = false) // Columna FK en la tabla 'eventos'
    @JsonBackReference("discoteca-eventos") // Parte "hija" de la relación bidireccional con Discoteca
    private Discoteca discoteca;

    @ManyToOne(fetch = FetchType.LAZY) // Mantenemos LAZY
    @JoinColumn(name = "id_admin", nullable = false) // Columna FK en la tabla 'eventos'
    @JsonBackReference("administrador-eventos") // Parte "hija" de la relación bidireccional con Administradores
    private Administradores administrador;

    @Column(name = "nombre_evento", nullable = false, length = 100)
    private String nombreEvento;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha", nullable = false, length = 20) // Coincide con VARCHAR(20) en SQL
    private String fecha;

    @Column(name = "hora", nullable = false, length = 10) // Coincide con VARCHAR(10) en SQL
    private String hora;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "imagen", length = 255) // Puede ser LONGTEXT en DB, String aquí lo maneja
    private String imagen;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("evento-reservas") // Parte "padre" de la relación bidireccional con Reserva
    private List<Reserva> reservas;

    // --- Constructor por defecto (necesario para JPA) ---
    public Evento() {
    }

    // --- Getters y Setters ---
    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public Discoteca getDiscoteca() {
        return discoteca;
    }

    public void setDiscoteca(Discoteca discoteca) {
        this.discoteca = discoteca;
    }

    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "idEvento=" + idEvento +
                ", nombreEvento='" + nombreEvento + '\'' +
                ", discotecaNit=" + (discoteca != null ? discoteca.getNit() : "null") +
                ", administradorId=" + (administrador != null ? administrador.getIdAdmin() : "null") +
                '}';
    }
}