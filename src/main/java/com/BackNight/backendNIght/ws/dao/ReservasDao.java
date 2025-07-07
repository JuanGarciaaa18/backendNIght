package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List;
import java.util.Optional;

@Service
public class ReservasDao {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public Reserva consultarReservaIndividual(Integer id) {
        return reservaRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public List<Reserva> obtenerTodasReservas() {
        return reservaRepository.findAll();
    }

    // --- ¡MÉTODO CRÍTICO QUE FALTABA! ---
    // Este método es llamado por ReservaService para obtener las reservas de un cliente.
    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public List<Reserva> obtenerReservasPorCliente(Integer idCliente) {
        // Llama al método del repositorio que usa JOIN FETCH para cargar el evento
        return reservaRepository.findByClienteIdClienteWithEvento(idCliente);
    }

    @Transactional // Añadido para operaciones de escritura
    public Reserva registrarReserva(Reserva reserva) {
        if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null) {
            Clientes cliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
            reserva.setCliente(cliente);
        } else {
            throw new RuntimeException("El ID del cliente es requerido para registrar una reserva.");
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

    @Transactional // Añadido para operaciones de escritura
    public Reserva actualizarReserva(Reserva reserva) {
        Optional<Reserva> existingReservaOpt = reservaRepository.findById(reserva.getIdReserva());
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            // Si el cliente se envía para actualizar y es diferente, buscar y asignar el nuevo cliente
            if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null &&
                    !reserva.getCliente().getIdCliente().equals(existingReserva.getCliente().getIdCliente())) {
                Clientes nuevoCliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                        .orElseThrow(() -> new RuntimeException("Nuevo cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
                existingReserva.setCliente(nuevoCliente);
            }
            // Si el evento se envía para actualizar y es diferente, buscar y asignar el nuevo evento
            // (Necesitarías inyectar EventoRepository aquí si permites cambiar el evento de una reserva existente)
            // if (reserva.getEvento() != null && reserva.getEvento().getIdEvento() != null &&
            //     !reserva.getEvento().getIdEvento().equals(existingReserva.getEvento().getIdEvento())) {
            //     Evento nuevoEvento = eventoRepository.findById(reserva.getEvento().getIdEvento())
            //             .orElseThrow(() -> new RuntimeException("Nuevo evento no encontrado con ID: " + reserva.getEvento().getIdEvento()));
            //     existingReserva.setEvento(nuevoEvento);
            // }

            // Actualizar solo los campos que se proporcionan en el objeto 'reserva'
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

    @Transactional // Añadido para operaciones de escritura
    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(idReserva);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstadoPago(nuevoEstadoPago);
            return reservaRepository.save(reserva);
        }
        return null;
    }

    @Transactional // Añadido para operaciones de escritura
    public boolean eliminarReserva(Integer id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}