// src/main/java/com/BackNight/backendNIght/ws/dto/LoginRequestDTO.java
package com.BackNight.backendNIght.ws.dto;

public class LoginRequestDTO {
    private String usuario; // Campo para el nombre de usuario o ID de usuario
    private String contrasena; // Campo para la contraseña

    // Constructor vacío (necesario para deserialización JSON)
    public LoginRequestDTO() {
    }

    // Constructor con todos los campos (opcional, pero buena práctica)
    public LoginRequestDTO(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}