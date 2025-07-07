package com.BackNight.backendNIght.ws.service;

import com.BackNight.backendNIght.ws.dao.ReservasDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
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
    private ClientesRepository clientesRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public ReservaDTO consultarReservaIndividualDTO(Integer id) {
        Reserva reserva = reservasDao.consultarReservaIndividual(id);
        // DEBUG: Imprimir la entidad Reserva antes de convertir a DTO
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
        // DEBUG: Imprimir detalles de cada reserva antes de mapear a DTO
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
        // DEBUG: Imprimir detalles de cada reserva para el cliente específico
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
        if (reserva.getCliente() == null || reserva.getCliente().getIdCliente() == null) {
            throw new RuntimeException("El ID del cliente es requerido para registrar una reserva.");
        }
        Clientes cliente = clientesRepository.findById(reserva.getCliente().getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getIdCliente()));
        reserva.setCliente(cliente);

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
        return reservasDao.actualizarReserva(reserva);
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
