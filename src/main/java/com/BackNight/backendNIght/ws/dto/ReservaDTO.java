package com.BackNight.backendNIght.ws.dto;

import lombok.AllArgsConstructor; // Para el constructor con todos los argumentos
import lombok.Data; // Para getters, setters, equals, hashCode, toString
import lombok.NoArgsConstructor; // Para el constructor sin argumentos (importante para Spring y serialización)

import java.math.BigDecimal;
import java.time.LocalDate;

@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con TODOS los campos
public class ReservaDTO {
    private Integer idReserva;
    private Integer idEvento;       // Coincide con el 2do argumento en ReservaService
    private String nombreEvento;    // Coincide con el 3er argumento
    private String nombreCliente;   // Coincide con el 4to argumento
    private Integer cantidadTickets; // Coincide con el 5to argumento
    private BigDecimal montoTotal;   // Coincide con el 6to argumento
    private String idTransaccion;   // Coincide con el 7mo argumento
    private String preferenceId;    // Coincide con el 8vo argumento
    private LocalDate fechaReserva; // Coincide con el 9no argumento
    private String estado;          // Coincide con el 10mo argumento
    private String estadoPago;      // Coincide con el 11vo argumento




    // Nota: El orden de los campos aquí es el que determina el orden de los argumentos
    // para el constructor generado por @AllArgsConstructor.
    // Debe coincidir con el orden en que los pasas en ReservaService.java.
}