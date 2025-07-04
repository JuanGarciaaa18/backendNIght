package com.BackNight.backendNIght.ws.entity;

import jakarta.persistence.*; // Asegúrate de tener todas las importaciones necesarias
// import com.fasterxml.jackson.annotation.JsonBackReference; // Podrías necesitar esto si tienes problemas de serialización bidireccional en otros lugares
// import com.fasterxml.jackson.annotation.JsonManagedReference; // Y esto también

@Entity
@Table(name = "eventos") // Asegúrate de que el nombre de la tabla sea correcto
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento") // Asegúrate de que el nombre de la columna sea correcto
    private Integer idEvento;

    @ManyToOne(fetch = FetchType.EAGER) // *** ESTE ES EL CAMBIO CLAVE ***
    @JoinColumn(name = "id_discoteca", nullable = false) // Asegúrate de que el nombre de la columna de la clave foránea sea correcto
    private Discoteca discoteca;

    @ManyToOne(fetch = FetchType.LAZY) // Asumiendo que el administrador no necesita cargarse siempre con el evento
    @JoinColumn(name = "id_administrador", nullable = false) // Asegúrate de que el nombre de la columna de la clave foránea sea correcto
    private Administradores administrador;

    @Column(name = "nombre_evento", nullable = false, length = 100)
    private String nombreEvento;

    @Column(name = "fecha", nullable = false, length = 10) // Considera usar java.time.LocalDate para fechas
    private String fecha;

    @Column(name = "hora", nullable = false, length = 5) // Considera usar java.time.LocalTime para horas
    private String hora;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "precio")
    private Double precio; // Usar Double o BigDecimal para precios es mejor

    @Column(name = "imagen", length = 255)
    private String imagen;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    // Opcional: toString() para depuración
    @Override
    public String toString() {
        return "Evento{" +
                "idEvento=" + idEvento +
                ", discoteca=" + (discoteca != null ? discoteca.getNit() : "null") + // Mostrar NIT si discoteca no es null
                ", nombreEvento='" + nombreEvento + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}