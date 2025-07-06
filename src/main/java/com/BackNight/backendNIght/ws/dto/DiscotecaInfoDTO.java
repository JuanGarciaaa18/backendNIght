package com.BackNight.backendNIght.ws.dto;

public class DiscotecaInfoDTO {
    private Integer nit;
    private String nombre;

    public DiscotecaInfoDTO() {}

    public DiscotecaInfoDTO(Integer nit, String nombre) {

        this.nit = nit;
        this.nombre = nombre;
    }
    // Getters y Setters
    public Integer getNit() { return nit; }
    public void setNit(Integer nit) { this.nit = nit; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}