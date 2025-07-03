// src/main/java/com/BackNight/backendNIght/ws/dto/ClienteLoginRequestDTO.java
package com.BackNight.backendNIght.ws.dto;

public class ClienteLoginRequestDTO {
    // Estos nombres de campo DEBEN coincidir con los que envías desde el frontend
    private String usuarioCliente;
    private String contrasenaCliente;

    // Constructor vacío (necesario para la deserialización de JSON por Spring)
    public ClienteLoginRequestDTO() {
    }

    // Constructor con todos los campos (opcional, pero buena práctica)
    public ClienteLoginRequestDTO(String usuarioCliente, String contrasenaCliente) {
        this.usuarioCliente = usuarioCliente;
        this.contrasenaCliente = contrasenaCliente;
    }

    // Getters y Setters
    public String getUsuarioCliente() {
        return usuarioCliente;
    }

    public void setUsuarioCliente(String usuarioCliente) {
        this.usuarioCliente = usuarioCliente;
    }

    public String getContrasenaCliente() {
        return contrasenaCliente;
    }

    public void setContrasenaCliente(String contrasenaCliente) {
        this.contrasenaCliente = contrasenaCliente;
    }
}