package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal; // Importa BigDecimal
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvento;

    private String nombreEvento;
    private String descripcion;
    private String fecha;
    private String hora;

    // **CAMBIO IMPORTANTE:** Usar BigDecimal para precios
    @Column(name = "precio")
    private BigDecimal precio;

    private String imagen;

    // Relación con Discoteca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nit_discoteca") // ¡Asegúrate que esta columna exista y esté correcta en tu DB!
    @JsonBackReference("discoteca-eventos")
    private Discoteca discoteca;

    // Relación con Administradores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin") // ¡Asegúrate que esta columna exista y esté correcta en tu DB!
    @JsonBackReference("administrador-eventos")
    private Administradores administrador;

    // Relación con Reservas
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("evento-reservas")
    private List<Reserva> reservas;

    // Constructor por defecto (necesario para JPA)
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

    public BigDecimal getPrecio() { // Tipo de retorno BigDecimal
        return precio;
    }

    public void setPrecio(BigDecimal precio) { // Parámetro BigDecimal
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