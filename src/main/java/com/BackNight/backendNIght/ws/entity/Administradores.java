package com.BackNight.backendNIght.ws.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "administrador")
public class Administradores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;

    @Column(name = "nombre_admin")
    private String nombreAdmin;

    @Column(name = "telefono_admin")
    private String telefonoAdmin;

    @Column(name = "correo_admin")
    private String correoAdmin;

    @Column(name = "usuario_admin")
    private String usuarioAdmin;

    @Column(name = "contrasena_admin")
    private String contrasenaAdmin;

    // Relación One-to-Many con Discoteca
    // 'mappedBy' indica el campo en la entidad Discoteca que posee la relación (administrador)
    // CascadeType.ALL significa que las operaciones (persist, remove, merge) se propagarán
    // orphanRemoval = true elimina las discotecas si se desvinculan del admin
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discoteca> discotecas = new ArrayList<>();

    // Relación One-to-Many con Evento
    // 'mappedBy' indica el campo en la entidad Evento que posee la relación (administrador)
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();

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

    public String getUsuarioAdmin() {
        return usuarioAdmin;
    }

    public void setUsuarioAdmin(String usuarioAdmin) {
        this.usuarioAdmin = usuarioAdmin;
    }

    public String getContrasenaAdmin() {
        return contrasenaAdmin;
    }

    public void setContrasenaAdmin(String contrasenaAdmin) {
        this.contrasenaAdmin = contrasenaAdmin;
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
}
