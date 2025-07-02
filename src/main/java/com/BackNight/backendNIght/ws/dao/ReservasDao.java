// src/main/java/com/BackNight/backendNIght/ws/dao/ReservasDao.java
package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservasDao {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    public Reserva consultarReservaIndividual(Integer id) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva != null) {
            limpiarReferenciasReserva(reserva);
        }
        return reserva;
    }

    public List<Reserva> obtenerTodasReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        for (Reserva r : reservas) {
            limpiarReferenciasReserva(r);
        }
        return reservas;
    }

    /**
     * Registra una nueva reserva.
     * Si no se especifica un estado de pago, se establece como 'PENDIENTE'.
     * @param reserva La reserva a registrar. Debe contener el ID del cliente.
     * @return La reserva registrada.
     * @throws RuntimeException Si el cliente no es encontrado.
     */
    public Reserva registrarReserva(Reserva reserva) {
        if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null) {
            Clientes cliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
            reserva.setCliente(cliente);
        } else {
            throw new RuntimeException("El ID del cliente es requerido para registrar una reserva.");
        }

        // Establecer estadoPago por defecto si no viene en la solicitud
        if (reserva.getEstadoPago() == null || reserva.getEstadoPago().isEmpty()) {
            reserva.setEstadoPago("PENDIENTE");
        }

        Reserva savedReserva = reservaRepository.save(reserva);
        limpiarReferenciasReserva(savedReserva);
        return savedReserva;
    }

    /**
     * Actualiza una reserva existente.
     * @param reserva La reserva con los datos actualizados. Debe contener el ID de la reserva.
     * @return La reserva actualizada o null si no existe.
     */
    public Reserva actualizarReserva(Reserva reserva) {
        Optional<Reserva> existingReservaOpt = reservaRepository.findById(reserva.getIdReserva());
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null &&
                    !reserva.getCliente().getIdCliente().equals(existingReserva.getCliente().getIdCliente())) {
                Clientes nuevoCliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                        .orElseThrow(() -> new RuntimeException("Nuevo cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
                reserva.setCliente(nuevoCliente);
            } else if (existingReserva.getCliente() != null) {
                reserva.setCliente(existingReserva.getCliente());
            } else {
                throw new RuntimeException("El ID del cliente es requerido para actualizar una reserva.");
            }

            // Actualizar otros campos de la reserva
            reserva.setFechaReserva(reserva.getFechaReserva() != null ? reserva.getFechaReserva() : existingReserva.getFechaReserva());
            reserva.setEstado(reserva.getEstado() != null ? reserva.getEstado() : existingReserva.getEstado());
            reserva.setEstadoPago(reserva.getEstadoPago() != null ? reserva.getEstadoPago() : existingReserva.getEstadoPago()); // ¡ACTUALIZAR ESTADOPAGO!

            Reserva updatedReserva = reservaRepository.save(reserva);
            limpiarReferenciasReserva(updatedReserva);
            return updatedReserva;
        }
        return null;
    }

    /**
     * Actualiza solo el estado de pago de una reserva específica.
     * @param idReserva El ID de la reserva a actualizar.
     * @param nuevoEstadoPago El nuevo estado de pago (ej. "PAGADO", "PENDIENTE", "FALLIDO").
     * @return La reserva actualizada o null si no existe.
     */
    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(idReserva);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstadoPago(nuevoEstadoPago);
            Reserva updatedReserva = reservaRepository.save(reserva);
            limpiarReferenciasReserva(updatedReserva);
            return updatedReserva;
        }
        return null;
    }

    public boolean eliminarReserva(Integer id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void limpiarReferenciasReserva(Reserva reserva) {
        // No se necesita limpiar nada si no hay relaciones circulares para el cliente
        // Si hay, puedes hacer reserva.getCliente().setContrasena(null); etc.
    }
}