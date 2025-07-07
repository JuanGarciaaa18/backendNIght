package com.BackNight.backendNIght.ws.service;

import com.BackNight.backendNIght.ws.dao.ReservasDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository; // Necesario para findByUsuarioCliente
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import com.BackNight.backendNIght.ws.dto.ReservaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservasDao reservasDao;

    @Autowired
    private ClientesRepository clientesRepository; // Inyectar ClientesRepository para buscar por usuario

    @Autowired
    private EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public ReservaDTO consultarReservaIndividualDTO(Integer id) {
        Reserva reserva = reservasDao.consultarReservaIndividual(id);
        if (reserva != null && reserva.getCliente() != null) {
            System.out.println("DEBUG: Consultar Reserva Individual - Cliente ID: " + reserva.getCliente().getIdCliente() +
                    ", Nombre: " + reserva.getCliente().getNombre() +
                    ", Usuario: " + reserva.getCliente().getUsuarioCliente());
        } else if (reserva != null) {
            System.out.println("DEBUG: Consultar Reserva Individual - Cliente es NULL");
        }
        return reserva != null ? new ReservaDTO(reserva) : null;
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerTodasLasReservasDTO() {
        List<Reserva> reservas = reservasDao.obtenerTodasReservas();
        System.out.println("DEBUG: Obteniendo Todas las Reservas. Cantidad: " + reservas.size());
        for (Reserva r : reservas) {
            if (r.getCliente() != null) {
                System.out.println("  Reserva ID: " + r.getIdReserva() +
                        ", Cliente ID: " + r.getCliente().getIdCliente() +
                        ", Nombre Cliente: " + r.getCliente().getNombre() +
                        ", Usuario Cliente: " + r.getCliente().getUsuarioCliente());
            } else {
                System.out.println("  Reserva ID: " + r.getIdReserva() + ", Cliente es NULL");
            }
        }
        return reservas.stream()
                .map(ReservaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerMisReservasDTO(Integer idCliente) {
        List<Reserva> reservas = reservasDao.obtenerReservasPorCliente(idCliente);
        System.out.println("DEBUG: Obteniendo Reservas para Cliente ID: " + idCliente + ". Cantidad: " + reservas.size());
        for (Reserva r : reservas) {
            if (r.getCliente() != null) {
                System.out.println("  Reserva ID: " + r.getIdReserva() +
                        ", Cliente ID: " + r.getCliente().getIdCliente() +
                        ", Nombre Cliente: " + r.getCliente().getNombre() +
                        ", Usuario Cliente: " + r.getCliente().getUsuarioCliente());
            } else {
                System.out.println("  Reserva ID: " + r.getIdReserva() + ", Cliente es NULL (¡Esto no debería pasar con FETCH!)");
            }
        }
        return reservas.stream()
                .map(ReservaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Reserva registrarReserva(Reserva reserva) {
        // CAMBIO CLAVE AQUÍ: Validar y buscar por usuarioCliente
        if (reserva.getCliente() == null || reserva.getCliente().getUsuarioCliente() == null || reserva.getCliente().getUsuarioCliente().isBlank()) {
            throw new RuntimeException("El usuario del cliente es requerido para registrar una reserva.");
        }
        Clientes cliente = clientesRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
        }
        reserva.setCliente(cliente); // Asignar el objeto Cliente completo

        if (reserva.getEvento() == null || reserva.getEvento().getIdEvento() == null) {
            throw new RuntimeException("El ID del evento es requerido para registrar una reserva.");
        }
        Evento evento = eventoRepository.findById(reserva.getEvento().getIdEvento())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + reserva.getEvento().getIdEvento()));
        reserva.setEvento(evento);

        if (reserva.getFechaReserva() == null) {
            reserva.setFechaReserva(LocalDate.now());
        }
        if (reserva.getEstado() == null || reserva.getEstado().isEmpty()) {
            reserva.setEstado("ACTIVA");
        }
        if (reserva.getEstadoPago() == null || reserva.getEstadoPago().isEmpty()) {
            reserva.setEstadoPago("PENDIENTE");
        }

        return reservasDao.registrarReserva(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Reserva reserva) {
        // CORRECCIÓN AQUÍ: Usar consultarReservaIndividual en lugar de findById
        Optional<Reserva> existingReservaOpt = Optional.ofNullable(reservasDao.consultarReservaIndividual(reserva.getIdReserva()));
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            // CAMBIO CLAVE AQUÍ: Si se proporciona un usuarioCliente en la actualización
            if (reserva.getCliente() != null && reserva.getCliente().getUsuarioCliente() != null &&
                    !reserva.getCliente().getUsuarioCliente().isBlank() &&
                    // Solo buscar si el usuarioCliente es diferente al existente
                    !reserva.getCliente().getUsuarioCliente().equals(existingReserva.getCliente().getUsuarioCliente())) {

                Clientes nuevoCliente = clientesRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
                if (nuevoCliente == null) {
                    throw new RuntimeException("Nuevo cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
                }
                existingReserva.setCliente(nuevoCliente); // Asignar el nuevo objeto Cliente
            } else if (existingReserva.getCliente() == null && reserva.getCliente() != null && reserva.getCliente().getUsuarioCliente() != null && !reserva.getCliente().getUsuarioCliente().isBlank()) {
                // Caso en que la reserva existente no tenía cliente pero ahora se le asigna uno
                Clientes nuevoCliente = clientesRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
                if (nuevoCliente == null) {
                    throw new RuntimeException("Cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
                }
                existingReserva.setCliente(nuevoCliente);
            }
            // Si no se proporciona usuarioCliente o es el mismo, se mantiene el cliente existente.

            // Actualizar solo los campos que se proporcionan en el objeto 'reserva'
            if (reserva.getFechaReserva() != null) existingReserva.setFechaReserva(reserva.getFechaReserva());
            if (reserva.getEstado() != null) existingReserva.setEstado(reserva.getEstado());
            if (reserva.getEstadoPago() != null) existingReserva.setEstadoPago(reserva.getEstadoPago());
            if (reserva.getCantidadTickets() != null) existingReserva.setCantidadTickets(reserva.getCantidadTickets());
            if (reserva.getIdTransaccion() != null) existingReserva.setIdTransaccion(reserva.getIdTransaccion());
            if (reserva.getMontoTotal() != null) existingReserva.setMontoTotal(reserva.getMontoTotal());
            if (reserva.getPreferenceId() != null) existingReserva.setPreferenceId(reserva.getPreferenceId());

            return reservasDao.actualizarReserva(existingReserva);
        }
        return null;
    }

    @Transactional
    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        return reservasDao.actualizarEstadoPagoReserva(idReserva, nuevoEstadoPago);
    }

    @Transactional
    public boolean eliminarReserva(Integer id) {
        return reservasDao.eliminarReserva(id);
    }
}
