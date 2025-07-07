package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservasDao {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClientesRepository clienteRepository; // Asegúrate de que este inyectado

    @Transactional(readOnly = true)
    public Reserva consultarReservaIndividual(Integer id) {
        return reservaRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodasReservas() {
        return reservaRepository.findAllWithEventoAndCliente();
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerReservasPorCliente(Integer idCliente) {
        return reservaRepository.findByClienteIdClienteWithEvento(idCliente);
    }

    /**
     * Método para registrar una reserva.
     * Si la entidad Reserva ya tiene un objeto Cliente asignado, lo usa.
     * Si no, busca el cliente por usuarioCliente (para uso de admin).
     * @param reserva La entidad Reserva a guardar.
     * @return La Reserva guardada.
     */
    @Transactional
    public Reserva registrarReserva(Reserva reserva) {
        // Si la reserva ya tiene un cliente asignado (ej. desde registrarReservaParaCliente), lo usa.
        // De lo contrario, busca el cliente por usuarioCliente (flujo del admin).
        if (reserva.getCliente() == null || reserva.getCliente().getIdCliente() == null) {
            if (reserva.getCliente() == null || reserva.getCliente().getUsuarioCliente() == null || reserva.getCliente().getUsuarioCliente().isBlank()) {
                throw new RuntimeException("El cliente o su usuario es requerido para registrar una reserva.");
            }
            Clientes cliente = clienteRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
            }
            reserva.setCliente(cliente);
        } else {
            // Asegurarse de que el cliente asignado es una entidad gestionada
            Clientes clienteExistente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente asignado a la reserva no encontrado en DB con ID: " + reserva.getCliente().getIdCliente()));
            reserva.setCliente(clienteExistente);
        }

        // Si el evento no está completamente cargado en la reserva, necesitas cargarlo aquí
        // Esto es importante si el frontend solo envía el idEvento
        // @Autowired private EventoRepository eventoRepository; // Necesitarías inyectar esto
        // if (reserva.getEvento() != null && reserva.getEvento().getIdEvento() != null) {
        //     Evento evento = eventoRepository.findById(reserva.getEvento().getIdEvento())
        //             .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + reserva.getEvento().getIdEvento()));
        //     reserva.setEvento(evento);
        // }

        if (reserva.getEstadoPago() == null || reserva.getEstadoPago().isEmpty()) {
            reserva.setEstadoPago("PENDIENTE");
        }

        return reservaRepository.save(reserva);
    }

    /**
     * Nuevo método para simplemente guardar una entidad Reserva.
     * Asume que todas las relaciones (Cliente, Evento) ya están correctamente configuradas en la entidad.
     * @param reserva La entidad Reserva a guardar.
     * @return La Reserva guardada.
     */
    @Transactional
    public Reserva saveReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }


    @Transactional
    public Reserva actualizarReserva(Reserva reserva) {
        Optional<Reserva> existingReservaOpt = Optional.ofNullable(consultarReservaIndividual(reserva.getIdReserva()));
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            if (reserva.getCliente() != null && reserva.getCliente().getUsuarioCliente() != null &&
                    !reserva.getCliente().getUsuarioCliente().isBlank() &&
                    !reserva.getCliente().getUsuarioCliente().equals(existingReserva.getCliente().getUsuarioCliente())) {

                Clientes nuevoCliente = clienteRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
                if (nuevoCliente == null) {
                    throw new RuntimeException("Nuevo cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
                }
                existingReserva.setCliente(nuevoCliente);
            } else if (existingReserva.getCliente() == null && reserva.getCliente() != null && reserva.getCliente().getUsuarioCliente() != null && !reserva.getCliente().getUsuarioCliente().isBlank()) {
                Clientes nuevoCliente = clienteRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
                if (nuevoCliente == null) {
                    throw new RuntimeException("Cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
                }
                existingReserva.setCliente(nuevoCliente);
            }

            if (reserva.getFechaReserva() != null) existingReserva.setFechaReserva(reserva.getFechaReserva());
            if (reserva.getEstado() != null) existingReserva.setEstado(reserva.getEstado());
            if (reserva.getEstadoPago() != null) existingReserva.setEstadoPago(reserva.getEstadoPago());
            if (reserva.getCantidadTickets() != null) existingReserva.setCantidadTickets(reserva.getCantidadTickets());
            if (reserva.getIdTransaccion() != null) existingReserva.setIdTransaccion(reserva.getIdTransaccion());
            if (reserva.getMontoTotal() != null) existingReserva.setMontoTotal(reserva.getMontoTotal());
            if (reserva.getPreferenceId() != null) existingReserva.setPreferenceId(reserva.getPreferenceId());

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
}
