package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal; // Importa BigDecimal
import java.time.LocalDate; // Usar LocalDate para la fecha del evento
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
    private String descripcion; // Nombre del campo es 'descripcion'

    @Column(name = "fecha")
    private LocalDate fecha; // Tipo de dato LocalDate para la fecha del evento

    @Column(name = "hora")
    private String hora;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "imagen", columnDefinition = "LONGTEXT")
    private String imagen;

    @Column(name = "ciudad") // Nuevo campo para la ciudad
    private String ciudad;

    @Column(name = "lugar") // Nuevo campo para el lugar
    private String lugar;

    @Column(name = "direccion") // Nuevo campo para la dirección
    private String direccion;

    @Column(name = "tipo") // Nuevo campo para el tipo de evento
    private String tipo;


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

    public String getDescripcion() { // Getter para 'descripcion'
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() { // Getter para 'fecha' (LocalDate)
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
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

    public String getCiudad() { // Getter para 'ciudad'
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getLugar() { // Getter para 'lugar'
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getDireccion() { // Getter para 'direccion'
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() { // Getter para 'tipo'
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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