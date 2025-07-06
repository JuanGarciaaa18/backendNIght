package com.BackNight.backendNIght.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Integer idReserva;
    private LocalDate fechaReserva;
    private String estado;
    private String estadoPago;
    private Integer cantidadTickets;
    private String idTransaccion;
    private BigDecimal montoTotal;
    private String preferenceId;

    // Campos para Evento y Cliente (IDs para la tabla del admin)
    private Integer idEvento;
    private String nombreEvento; // Opcional, si quieres mostrar el nombre del evento
    private Integer idUsuario;
    private String nombreUsuario; // Opcional, si quieres mostrar el nombre del usuario
}