package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "eventos") // Asegúrate de que el nombre de la tabla sea 'eventos'
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvento;

    @Column(name = "nombre_evento") // Mapeo explícito para claridad
    private String nombreEvento;

    @Column(name = "descripcion", columnDefinition = "TEXT") // Mapeo explícito a TEXT
    private String descripcion;

    @Column(name = "fecha", length = 20) // Mapeo explícito y longitud
    private String fecha;

    @Column(name = "hora", length = 10) // Mapeo explícito y longitud
    private String hora;

    @Column(name = "precio", precision = 10, scale = 2) // Mapeo explícito para DECIMAL
    private double precio;

    @Column(name = "imagen", columnDefinition = "LONGTEXT") // Mapeo explícito a LONGTEXT
    private String imagen;

    // Relación Many-to-One con Discoteca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nit_discoteca") // Columna de clave foránea en la tabla 'eventos'
    @JsonBackReference("discoteca-eventos") // Parte "hija" de la relación bidireccional
    private Discoteca discoteca;

    // Relación Many-to-One con Administradores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false) // Columna de clave foránea en la tabla 'eventos'. Agregamos nullable=false aquí para que JPA intente hacerla NOT NULL.
    @JsonBackReference("administrador-eventos") // Parte "hija" de la relación bidireccional
    private Administradores administrador;

    // Relación One-to-Many con Reserva
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("evento-reservas") // Parte "padre" de la relación bidireccional
    private List<Reserva> reservas;

    // --- Constructor vacío (necesario para JPA) ---
    public Evento() {
    }

    // --- Getters y Setters ---

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}