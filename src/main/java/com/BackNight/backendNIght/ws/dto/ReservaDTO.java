package com.BackNight.backendNIght.ws.dto;

import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.entity.Evento; // Asegúrate de importar Evento
import com.BackNight.backendNIght.ws.entity.Clientes; // Asegúrate de importar Clientes

import java.math.BigDecimal;
import java.time.LocalDate; // Mantenemos LocalDate para fechaReserva, pero fechaEvento será String

public class ReservaDTO {
    private Integer idReserva;
    private LocalDate fechaReserva; // Sigue siendo LocalDate
    private String estado;
    private String estadoPago;
    private Integer cantidadTickets;
    private String idTransaccion;
    private BigDecimal montoTotal;
    private String preferenceId;

    // Campos relacionados con el Evento (solo lo necesario para el DTO)
    private Integer idEvento;
    private String nombreEvento;
    private String descripcionEvento;
    private String fechaEvento; // CAMBIO: Ahora es String
    // ¡ELIMINADOS! Estos campos no existen en la entidad Evento
    // private String ciudadEvento;
    // private String lugarEvento;
    // private String direccionEvento;
    // private String tipoEvento;
    private BigDecimal precioEvento;
    private String imagenEvento;


    // Campos relacionados con el Cliente (solo lo necesario para el DTO)
    private Integer idCliente;
    private String nombreCliente;
    private String correoCliente;
    private String usuarioCliente;


    // 1. Constructor vacío (necesario para algunos frameworks/librerías)
    public ReservaDTO() {
    }

    // 2. Constructor CRÍTICO: Construye el DTO a partir de la entidad Reserva
    public ReservaDTO(Reserva reserva) {
        this.idReserva = reserva.getIdReserva();
        this.fechaReserva = reserva.getFechaReserva();
        this.estado = reserva.getEstado();
        this.estadoPago = reserva.getEstadoPago();
        this.cantidadTickets = reserva.getCantidadTickets();
        this.idTransaccion = reserva.getIdTransaccion();
        this.montoTotal = reserva.getMontoTotal();
        this.preferenceId = reserva.getPreferenceId();

        // Mapea la información del Evento si existe
        if (reserva.getEvento() != null) {
            this.idEvento = reserva.getEvento().getIdEvento();
            this.nombreEvento = reserva.getEvento().getNombreEvento();
            this.descripcionEvento = reserva.getEvento().getDescripcion();
            this.fechaEvento = reserva.getEvento().getFecha(); // CAMBIO: Asignamos String directamente

            // ¡ELIMINADOS! No se mapean porque no existen en Evento.java
            // this.ciudadEvento = reserva.getEvento().getCiudad();
            // this.lugarEvento = reserva.getEvento().getLugar();
            // this.direccionEvento = reserva.getEvento().getDireccion();
            // this.tipoEvento = reserva.getEvento().getTipo();

            this.precioEvento = reserva.getEvento().getPrecio();
            this.imagenEvento = reserva.getEvento().getImagen();

        }

        // Mapea la información del Cliente si existe
        if (reserva.getCliente() != null) {
            this.idCliente = reserva.getCliente().getIdCliente();
            this.nombreCliente = reserva.getCliente().getNombre();
            this.correoCliente = reserva.getCliente().getCorreo();
            this.usuarioCliente = reserva.getCliente().getUsuarioCliente();
        }
    }

    // 3. Constructor con todos los parámetros (ajustado a los nuevos campos)
    public ReservaDTO(Integer idReserva, LocalDate fechaReserva, String estado, String estadoPago, Integer cantidadTickets, String idTransaccion, BigDecimal montoTotal, String preferenceId, Integer idEvento, String nombreEvento, String descripcionEvento, String fechaEvento, BigDecimal precioEvento, String imagenEvento, Integer idCliente, String nombreCliente, String correoCliente, String usuarioCliente) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.cantidadTickets = cantidadTickets;
        this.idTransaccion = idTransaccion;
        this.montoTotal = montoTotal;
        this.preferenceId = preferenceId;
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.descripcionEvento = descripcionEvento;
        this.fechaEvento = fechaEvento;
        this.precioEvento = precioEvento;
        this.imagenEvento = imagenEvento;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.usuarioCliente = usuarioCliente;
    }


    // --- GETTERS Y SETTERS ---
    // (Asegúrate de tener todos los getters y setters para todos los campos aquí)

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Integer getCantidadTickets() {
        return cantidadTickets;
    }

    public void setCantidadTickets(Integer cantidadTickets) {
        this.cantidadTickets = cantidadTickets;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

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

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }

    public String getFechaEvento() { // CAMBIO: Getter devuelve String
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) { // CAMBIO: Setter acepta String
        this.fechaEvento = fechaEvento;
    }

    // ¡ELIMINADOS! Getters y setters para ciudadEvento, lugarEvento, direccionEvento, tipoEvento
    // public String getCiudadEvento() { return ciudadEvento; }
    // public void setCiudadEvento(String ciudadEvento) { this.ciudadEvento = ciudadEvento; }
    // public String getLugarEvento() { return lugarEvento; }
    // public void setLugarEvento(String lugarEvento) { this.lugarEvento = lugarEvento; }
    // public String getDireccionEvento() { return direccionEvento; }
    // public void setDireccionEvento(String direccionEvento) { this.direccionEvento = direccionEvento; }
    // public String getTipoEvento() { return tipoEvento; }
    // public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public BigDecimal getPrecioEvento() {
        return precioEvento;
    }

    public void setPrecioEvento(BigDecimal precioEvento) {
        this.precioEvento = precioEvento;
    }

    public String getImagenEvento() {
        return imagenEvento;
    }

    public void setImagenEvento(String imagenEvento) {
        this.imagenEvento = imagenEvento;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getUsuarioCliente() {
        return usuarioCliente;
    }

    public void setUsuarioCliente(String usuarioCliente) {
        this.usuarioCliente = usuarioCliente;
    }
}
