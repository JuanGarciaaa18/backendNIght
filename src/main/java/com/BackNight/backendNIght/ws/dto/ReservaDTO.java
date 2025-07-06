package com.BackNight.backendNIght.ws.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Usamos Lombok para simplificar los getters, setters, constructores y toString
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getters, setters, toString, hashCode, equals
@Builder // Facilita la creación de objetos con un patrón de constructor
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class ReservaDTO {

    private Integer idReserva;
    private Integer idEvento;
    private String nombreEvento;
    private String nombreCliente;
    private Integer cantidadTickets;
    private BigDecimal montoTotal;
    private String idTransaccion;
    private String preferenceId;
    private LocalDate fechaReserva;
    private String estado;
    private String estadoPago;

    // Con @AllArgsConstructor, Lombok ya genera el constructor con todos los argumentos
    // que es el que necesita tu convertToDto. No necesitas escribirlo manualmente si usas Lombok.
    // Si no usaras Lombok, el constructor se vería así:
    /*
    public ReservaDTO(Integer idReserva, Integer idEvento, String nombreEvento, String nombreCliente,
                      Integer cantidadTickets, BigDecimal montoTotal, String idTransaccion,
                      String preferenceId, LocalDate fechaReserva, String estado, String estadoPago) {
        this.idReserva = idReserva;
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.nombreCliente = nombreCliente;
        this.cantidadTickets = cantidadTickets;
        this.montoTotal = montoTotal;
        this.idTransaccion = idTransaccion;
        this.preferenceId = preferenceId;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.estadoPago = estadoPago;
    }
    */
}