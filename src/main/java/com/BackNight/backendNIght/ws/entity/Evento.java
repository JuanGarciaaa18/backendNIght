package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonBackReference; // Puede que necesites esta
import com.fasterxml.jackson.annotation.JsonManagedReference; // Asegúrate de importar esta
import jakarta.persistence.*;
import java.util.List; // Importa List

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
    private double precio;
    private String imagen;

    // Relación con Discoteca
    @ManyToOne(fetch = FetchType.LAZY) // CAMBIO: A LAZY (si Discoteca tiene List<Evento>)
    @JoinColumn(name = "nit_discoteca")
    @JsonBackReference("discoteca-eventos") // AÑADIDO: Rompe el ciclo. Nombre debe coincidir en Discoteca
    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ELIMINAR
    private Discoteca discoteca;

    // Relación con Administradores
    @ManyToOne(fetch = FetchType.LAZY) // CAMBIO: A LAZY (si Administradores tiene List<Evento>)
    @JoinColumn(name = "id_admin")
    @JsonBackReference("administrador-eventos") // AÑADIDO: Rompe el ciclo. Nombre debe coincidir en Administradores
    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ELIMINAR
    private Administradores administrador;

    // RELACIÓN AÑADIDA: Un evento puede tener muchas reservas
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("evento-reservas") // AÑADIDO: Lado "padre" de la relación con Reserva
    private List<Reserva> reservas;

    // --- Getters y Setters ---
    // ... (Mantén tus getters y setters existentes, y añade para 'reservas')

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