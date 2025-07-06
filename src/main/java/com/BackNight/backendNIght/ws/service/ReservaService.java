package com.BackNight.backendNIght.ws.service;

import com.BackNight.backendNIght.ws.dao.ReservasDao;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.dto.ReservaDTO; // Asegúrate de que esta importación sea correcta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservasDao reservasDao;

    // Método para mapear una entidad Reserva a un DTO
    private ReservaDTO convertToDto(Reserva reserva) {
        String nombreEvento = (reserva.getEvento() != null) ? reserva.getEvento().getNombreEvento() : "N/A";
        // Asume que Cliente tiene getNombre(), si no, ajusta aquí.
        String nombreCliente = (reserva.getCliente() != null) ? reserva.getCliente().getNombre() : "N/A";
        Integer idEvento = (reserva.getEvento() != null) ? reserva.getEvento().getIdEvento() : null;

        // ¡Este orden y cantidad de argumentos ahora coincidirán con el constructor de ReservaDTO!
        return new ReservaDTO(
                reserva.getIdReserva(),
                idEvento,
                nombreEvento,
                nombreCliente,
                reserva.getCantidadTickets(),
                reserva.getMontoTotal(),
                reserva.getIdTransaccion(),
                reserva.getPreferenceId(),
                reserva.getFechaReserva(),
                reserva.getEstado(),
                reserva.getEstadoPago()
        );
    }

    public List<ReservaDTO> obtenerTodasLasReservasDTO() {
        List<Reserva> reservas = reservasDao.obtenerTodasReservas();
        return reservas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ReservaDTO consultarReservaIndividualDTO(Integer idReserva) {
        Reserva reserva = reservasDao.consultarReservaIndividual(idReserva);
        return (reserva != null) ? convertToDto(reserva) : null;
    }

    public Reserva registrarReserva(Reserva reserva) {
        return reservasDao.registrarReserva(reserva);
    }

    public Reserva actualizarReserva(Reserva reserva) {
        return reservasDao.actualizarReserva(reserva);
    }

    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        return reservasDao.actualizarEstadoPagoReserva(idReserva, nuevoEstadoPago);
    }

    public boolean eliminarReserva(Integer idReserva) {
        return reservasDao.eliminarReserva(idReserva);
    }
}