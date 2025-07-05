package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "administrador") // Asegúrate de que el nombre de la tabla sea 'administrador'
public class Administradores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdmin;

    @Column(name = "nombre_admin")
    private String nombreAdmin;

    @Column(name = "telefono_admin")
    private String telefonoAdmin;

    @Column(name = "correo_admin")
    private String correoAdmin;

    @Column(name = "usuario_admin")
    private String nombreUsuario; // Cambio a nombreUsuario para más claridad si es el que se usa para login

    @Column(name = "contrasena_admin")
    private String contrasena;

    // Relación One-to-Many con Discoteca (un administrador puede tener muchas discotecas)
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("administrador-discotecas") // Lado "padre" de la relación con Discoteca
    private List<Discoteca> discotecas;

    // Relación One-to-Many con Eventos (un administrador puede crear muchos eventos)
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("administrador-eventos") // Lado "padre" de la relación con Evento
    private List<Evento> eventos;

    // --- Constructor vacío ---
    public Administradores() {
    }

    // --- Getters y Setters ---

    public Integer getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getTelefonoAdmin() {
        return telefonoAdmin;
    }

    public void setTelefonoAdmin(String telefonoAdmin) {
        this.telefonoAdmin = telefonoAdmin;
    }

    public String getCorreoAdmin() {
        return correoAdmin;
    }

    public void setCorreoAdmin(String correoAdmin) {
        this.correoAdmin = correoAdmin;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Discoteca> getDiscotecas() {
        return discotecas;
    }

    public void setDiscotecas(List<Discoteca> discotecas) {
        this.discotecas = discotecas;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
    // No hay getters/setters para 'paquetes' porque la lista ha sido eliminada
}