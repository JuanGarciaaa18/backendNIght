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
        // La limpieza de referencias ya no es necesaria aquí si usas DTOs y @JsonBackReference
        // if (reserva != null) {
        //     limpiarReferenciasReserva(reserva);
        // }
        return reserva;
    }

    public List<Reserva> obtenerTodasReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        // La limpieza de referencias ya no es necesaria aquí
        // for (Reserva r : reservas) {
        //     limpiarReferenciasReserva(r);
        // }
        return reservas;
    }

    public Reserva registrarReserva(Reserva reserva) {
        if (reserva.getCliente() != null && reserva.getCliente().getIdCliente() != null) {
            Clientes cliente = clienteRepository.findById(reserva.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
            reserva.setCliente(cliente);
        } else {
            throw new RuntimeException("El ID del cliente es requerido para registrar una reserva.");
        }

        if (reserva.getEstadoPago() == null || reserva.getEstadoPago().isEmpty()) {
            reserva.setEstadoPago("PENDIENTE");
        }

        Reserva savedReserva = reservaRepository.save(reserva);
        // La limpieza de referencias ya no es necesaria aquí
        // limpiarReferenciasReserva(savedReserva);
        return savedReserva;
    }

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

            reserva.setFechaReserva(reserva.getFechaReserva() != null ? reserva.getFechaReserva() : existingReserva.getFechaReserva());
            reserva.setEstado(reserva.getEstado() != null ? reserva.getEstado() : existingReserva.getEstado());
            reserva.setEstadoPago(reserva.getEstadoPago() != null ? reserva.getEstadoPago() : existingReserva.getEstadoPago());

            Reserva updatedReserva = reservaRepository.save(reserva);
            // La limpieza de referencias ya no es necesaria aquí
            // limpiarReferenciasReserva(updatedReserva);
            return updatedReserva;
        }
        return null;
    }

    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(idReserva);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstadoPago(nuevoEstadoPago);
            Reserva updatedReserva = reservaRepository.save(reserva);
            // La limpieza de referencias ya no es necesaria aquí
            // limpiarReferenciasReserva(updatedReserva);
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

    // Este método ya no es necesario con @JsonBackReference y mapeo a DTOs
    // private void limpiarReferenciasReserva(Reserva reserva) { }
}