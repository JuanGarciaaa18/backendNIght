package com.BackNight.backendNIght.ws.vo;

public class DiscotecasVo {

    private String nit;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private String imagen; // Nueva propiedad para la imagen

    public DiscotecasVo(String nit, String nombre, String ubicacion, int capacidad, String imagen) {
        super();
        this.nit = nit;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.imagen = imagen;
    }

    public DiscotecasVo() {
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}