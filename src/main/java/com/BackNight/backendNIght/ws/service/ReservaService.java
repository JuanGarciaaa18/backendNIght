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

import java.math.BigDecimal;
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

    // --- Métodos para DTOs (usados por el controlador REST para admin y público) ---

    public ReservaDTO consultarReservaIndividualDTO(Integer id) {
        // Usa el método del repositorio que carga el evento y el cliente
        Optional<Reserva> optionalReserva = reservaRepository.findByIdWithEventoAndCliente(id);
        return optionalReserva.map(this::toDTO).orElse(null);
    }

    public List<ReservaDTO> obtenerTodasLasReservasDTO() {
        // Usa el método del repositorio que carga todos los eventos y clientes
        List<Reserva> reservas = reservaRepository.findAllWithEventoAndCliente();
        return reservas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Métodos que devuelven la entidad Reserva (usados internamente o por otros servicios) ---

    // Este método retorna la entidad, usado por ejemplo en MercadoPagoService
    @Transactional // Asegura que las relaciones se carguen si se accede a ellas en la misma transacción
    public Reserva consultarReservaIndividual(Integer id) {
        // Nota: Este método no usa el JOIN FETCH, si accedes a .getCliente() o .getEvento() fuera de esta transacción, podría dar LazyInitializationException
        return reservaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Reserva registrarReserva(Reserva reserva) {
        // Asocia el Cliente
        if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null) {
            Clientes cliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
            reserva.setCliente(cliente);
        } else {
            throw new RuntimeException("El ID del cliente es requerido para registrar una reserva.");
        }

        // Asocia el Evento
        if (reserva.getEvento() != null && reserva.getEvento().getIdEvento() != null) {
            Evento evento = eventoRepository.findById(reserva.getEvento().getIdEvento())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + reserva.getEvento().getIdEvento()));
            reserva.setEvento(evento);
        } else {
            throw new RuntimeException("El ID del evento es requerido para registrar una reserva.");
        }

        // Establecer estadoPago por defecto si no viene en la solicitud
        if (reserva.getEstadoPago() == null || reserva.getEstadoPago().isEmpty()) {
            reserva.setEstadoPago("PENDIENTE");
        }
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Reserva reserva) {
        Optional<Reserva> existingReservaOpt = reservaRepository.findById(reserva.getIdReserva());
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            // Actualiza solo los campos que vienen en 'reserva' y son diferentes
            if (reserva.getFechaReserva() != null) existingReserva.setFechaReserva(reserva.getFechaReserva());
            if (reserva.getEstado() != null) existingReserva.setEstado(reserva.getEstado());
            if (reserva.getEstadoPago() != null) existingReserva.setEstadoPago(reserva.getEstadoPago());
            if (reserva.getCantidadTickets() != null) existingReserva.setCantidadTickets(reserva.getCantidadTickets());
            if (reserva.getIdTransaccion() != null) existingReserva.setIdTransaccion(reserva.getIdTransaccion());
            if (reserva.getMontoTotal() != null) existingReserva.setMontoTotal(reserva.getMontoTotal());
            if (reserva.getPreferenceId() != null) existingReserva.setPreferenceId(reserva.getPreferenceId());

            // Actualizar Cliente si es diferente y no nulo
            if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null &&
                    (existingReserva.getCliente() == null || !reserva.getCliente().getIdCliente().equals(existingReserva.getCliente().getIdCliente()))) {
                Clientes nuevoCliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                        .orElseThrow(() -> new RuntimeException("Nuevo cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
                existingReserva.setCliente(nuevoCliente);
            }

            // Actualizar Evento si es diferente y no nulo
            if (reserva.getEvento() != null && reserva.getEvento().getIdEvento() != null &&
                    (existingReserva.getEvento() == null || !reserva.getEvento().getIdEvento().equals(existingReserva.getEvento().getIdEvento()))) {
                Evento nuevoEvento = eventoRepository.findById(reserva.getEvento().getIdEvento())
                        .orElseThrow(() -> new RuntimeException("Nuevo evento no encontrado con ID: " + reserva.getEvento().getIdEvento()));
                existingReserva.setEvento(nuevoEvento);
            }

            return reservaRepository.save(existingReserva);
        }
        return null;
    }

    @Transactional
    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(idReserva);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstadoPago(nuevoEstadoPago);
            return reservaRepository.save(reserva);
        }
        return null;
    }

    @Transactional
    public boolean eliminarReserva(Integer id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Actualiza el preferenceId y montoTotal de una reserva después de la creación de la preferencia en Mercado Pago.
     * @param reservaId El ID de la reserva a actualizar.
     * @param preferenceId El ID de preferencia devuelto por Mercado Pago.
     * @param totalAmount El monto total final enviado a Mercado Pago.
     */
    @Transactional
    public void updatePreferenceIdAndTotalAmount(Integer reservaId, String preferenceId, BigDecimal totalAmount) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setPreferenceId(preferenceId);
            reserva.setMontoTotal(totalAmount);
            reservaRepository.save(reserva);
        } else {
            throw new RuntimeException("Reserva con ID " + reservaId + " no encontrada para actualizar preferenceId.");
        }
    }


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
            // Asegúrate de que tu entidad Evento tiene un método getNombreEvento()
            // Si no lo tiene, usa otro campo o deja este como "N/A"
            dto.setNombreEvento(reserva.getEvento().getNombreEvento());
        } else {
            dto.setIdEvento(null);
            dto.setNombreEvento("N/A"); // Default si no hay evento
        }

        // Mapear Cliente (Usuario)
        if (reserva.getCliente() != null) {
            dto.setIdUsuario(reserva.getCliente().getIdCliente());
            // Usa getUsuarioCliente() que ya confirmamos que existe
            dto.setNombreUsuario(reserva.getCliente().getUsuarioCliente());
        } else {
            dto.setIdUsuario(null);
            dto.setNombreUsuario("N/A"); // Default si no hay cliente
        }
        return dto;
    }
}