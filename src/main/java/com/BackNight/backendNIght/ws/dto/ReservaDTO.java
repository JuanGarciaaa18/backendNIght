package com.BackNight.backendNIght.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // <-- ¡Importante! Asegúrate de que esta línea esté presente
import java.time.LocalDate; // Para fechaReserva, si lo usas en el DTO

@Data // Genera getters, setters, equals, hashCode, toString
@Builder // Permite construir objetos de forma fluida
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class ReservaDTO {
    private Integer idReserva;
    private Integer idEvento;
    private String nombreEvento;
    private String nombreCliente;
    private Integer cantidadTickets;
    private BigDecimal montoTotal; // <-- ¡ESTE ES EL CAMBIO CLAVE! De Double a BigDecimal
    private String idTransaccion;
    private String preferenceId;
    private LocalDate fechaReserva; // Considera usar LocalDate si la entidad lo usa
    private String estado;
    private String estadoPago;

    // Si no usas Lombok para @AllArgsConstructor, deberías tener un constructor así:
    /*
    public ReservaDTO(Integer idReserva, Integer idEvento, String nombreEvento, String nombreCliente,
                      Integer cantidadTickets, BigDecimal montoTotal, String idTransaccion,
                      String preferenceId, LocalDate fechaReserva, String estado, String estadoPago) {
        this.idReserva = idReserva;
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.nombreCliente = nombreCliente;
        this.cantidadTickets = cantidadTickets;
        this.montoTotal = montoTotal; // Asignación de BigDecimal a BigDecimal
        this.idTransaccion = idTransaccion;
        this.preferenceId = preferenceId;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.estadoPago = estadoPago;
    }
    */
}