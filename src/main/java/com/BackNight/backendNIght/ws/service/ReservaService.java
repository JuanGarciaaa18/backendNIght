package com.BackNight.backendNIght.ws.service;

import com.BackNight.backendNIght.ws.dto.ReservaDTO;
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import com.BackNight.backendNIght.ws.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    // ... (rest of your methods like consultarReservaIndividualDTO, obtenerTodasLasReservasDTO, etc.)

    // Método de mapeo de entidad a DTO (PRIVATE)
    private ReservaDTO toDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setEstado(reserva.getEstado());
        dto.setEstadoPago(reserva.getEstadoPago());
        dto.setCantidadTickets(reserva.getCantidadTickets());
        dto.setIdTransaccion(reserva.getIdTransaccion());
        dto.setMontoTotal(reserva.getMontoTotal());
        dto.setPreferenceId(reserva.getPreferenceId());

        // Mapear Evento
        if (reserva.getEvento() != null) {
            dto.setIdEvento(reserva.getEvento().getIdEvento());
            dto.setNombreEvento(reserva.getEvento().getNombreEvento()); // Assuming Evento has getNombreEvento()
        } else {
            dto.setIdEvento(null);
            dto.setNombreEvento("N/A");
        }

        // Mapear Cliente (Usuario)
        if (reserva.getCliente() != null) {
            dto.setIdUsuario(reserva.getCliente().getIdCliente());
            // *** CAMBIO CRÍTICO AQUÍ: Usar getUsuarioCliente() ***
            dto.setNombreUsuario(reserva.getCliente().getUsuarioCliente()); // <--- USE THIS METHOD!
        } else {
            dto.setIdUsuario(null);
            dto.setNombreUsuario("N/A");
        }
        return dto;
    }

    // ... (rest of your methods)
}