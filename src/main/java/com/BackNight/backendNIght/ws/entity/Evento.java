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
    @Column(name = "id_evento") // Asegúrate de tener el @Column para id_evento
    private Integer idEvento;

    @Column(name = "nombre_evento") // Añadir @Column si el nombre de la columna es diferente
    private String nombreEvento;
    @Column(name = "descripcion", columnDefinition = "TEXT") // Asegurar mapeo correcto
    private String descripcion;
    @Column(name = "fecha")
    private String fecha;
    @Column(name = "hora")
    private String hora;

    // **CAMBIO IMPORTANTE:** Usar BigDecimal para precios
    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "imagen", columnDefinition = "LONGTEXT") // Asegurar mapeo correcto
    private String imagen;

    // Relación con Discoteca
    @ManyToOne(fetch = FetchType.LAZY) // Mantener LAZY aquí, JOIN FETCH lo cargará
    @JoinColumn(name = "nit_discoteca", referencedColumnName = "nit", nullable = false) // ¡nullable=false si siempre debe tener discoteca!
    @JsonBackReference("discoteca-eventos")
    private Discoteca discoteca;

    // Relación con Administradores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", referencedColumnName = "id_admin", nullable = false) // ¡nullable=false si siempre debe tener admin!
    @JsonBackReference("administrador-eventos")
    private Administradores administrador;

    // Relación con Reservas
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("evento-reservas")
    private List<Reserva> reservas;

    // Constructor por defecto (necesario para JPA)
    public Evento() {
    }

    // --- Getters y Setters --- (Asegúrate de que todos estén, especialmente para las relaciones)
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