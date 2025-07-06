package com.BackNight.backendNIght.ws.service;

import com.BackNight.backendNIght.ws.dao.ReservasDao;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.dto.ReservaDTO;
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
        String nombreCliente = (reserva.getCliente() != null) ? reserva.getCliente().getNombre() : "N/A"; // Asume que Cliente tiene getNombre()
        Integer idEvento = (reserva.getEvento() != null) ? reserva.getEvento().getIdEvento() : null;

        // ¡Este orden y cantidad de argumentos debe coincidir con el constructor de ReservaDTO!
        return new ReservaDTO(
                reserva.getIdReserva(),            // 1er argumento (Integer)
                idEvento,                          // 2do argumento (Integer)
                nombreEvento,                      // 3er argumento (String)
                nombreCliente,                     // 4to argumento (String)
                reserva.getCantidadTickets(),      // 5to argumento (Integer)
                reserva.getMontoTotal(),           // 6to argumento (BigDecimal)
                reserva.getIdTransaccion(),        // 7mo argumento (String)
                reserva.getPreferenceId(),         // 8vo argumento (String)
                reserva.getFechaReserva(),         // 9no argumento (LocalDate)
                reserva.getEstado(),               // 10mo argumento (String)
                reserva.getEstadoPago()            // 11vo argumento (String)
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