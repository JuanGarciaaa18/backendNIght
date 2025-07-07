package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
// import java.time.LocalDate; // ¡ELIMINADO!
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(name = "nombre_evento")
    private String nombreEvento;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha")
    private String fecha; // CAMBIO: Ahora es String para coincidir con VARCHAR(20) en DB

    @Column(name = "hora")
    private String hora;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "imagen", columnDefinition = "LONGTEXT")
    private String imagen;

    // ¡ELIMINADOS! Estos campos no existen en tu tabla 'eventos' actual
    // private String ciudad;
    // private String lugar;
    // private String direccion;
    // private String tipo;


    // Relación con Discoteca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nit_discoteca", referencedColumnName = "nit", nullable = false)
    @JsonBackReference("discoteca-eventos")
    private Discoteca discoteca;

    // Relación con Administradores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", referencedColumnName = "id_admin", nullable = false)
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

    public String getFecha() { // CAMBIO: Getter devuelve String
        return fecha;
    }

    public void setFecha(String fecha) { // CAMBIO: Setter acepta String
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // ¡ELIMINADOS! Los getters y setters para ciudad, lugar, direccion, tipo
    // public String getCiudad() { return ciudad; }
    // public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    // public String getLugar() { return lugar; }
    // public void setLugar(String lugar) { this.lugar = lugar; }
    // public String getDireccion() { return direccion; }
    // public void setDireccion(String direccion) { this.direccion = direccion; }
    // public String getTipo() { return tipo; }
    // public void setTipo(String tipo) { this.tipo = tipo; }

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
