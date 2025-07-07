package com.BackNight.backendNIght.ws.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cliente") // ¡IMPORTANTE! Asegúrate de que el nombre de la tabla en tu DB sea 'cliente'
public class Clientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    private String nombre;
    private Integer edad;
    private String telefono;
    private String correo;

    @Column(name = "usuario_cliente")
    private String usuarioCliente;

    @Column(name = "contrasena_cliente")
    private String contrasenaCliente;

    // --- RELACIÓN CON RESERVAS ---
    // Un cliente puede tener muchas reservas
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cliente-reservas") // Para evitar recursión infinita en JSON
    private List<Reserva> reservas;

    public Clientes() {} // Constructor vacío necesario para JPA

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getUsuarioCliente() { return usuarioCliente; }
    public void setUsuarioCliente(String usuarioCliente) { this.usuarioCliente = usuarioCliente; }
    public String getContrasenaCliente() { return contrasenaCliente; }
    public void setContrasenaCliente(String contrasenaCliente) { this.contrasenaCliente = contrasenaCliente; }
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}
