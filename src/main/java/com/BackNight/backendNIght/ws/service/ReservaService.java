package com.BackNight.backendNIght.ws.service;

// Eliminamos la importación de ReservasDao, ya que lo reemplazaremos por ReservaRepository
// import com.BackNight.backendNIght.ws.dao.ReservasDao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.entity.Discoteca; // Importamos Discoteca
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import com.BackNight.backendNIght.ws.repository.ReservaRepository; // Usaremos este directamente
import com.BackNight.backendNIght.ws.repository.DiscotecaRepository; // Importamos DiscotecaRepository
import com.BackNight.backendNIght.ws.dto.ReservaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections; // Importamos para listas vacías
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    // Cambiamos a ReservaRepository y lo inyectamos
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private DiscotecaRepository discotecaRepository; // Inyectamos DiscotecaRepository

    // Si tienes un constructor explícito, actualízalo:
    // public ReservaService(ReservaRepository reservaRepository, ClientesRepository clientesRepository,
    //                       EventoRepository eventoRepository, DiscotecaRepository discotecaRepository) {
    //     this.reservaRepository = reservaRepository;
    //     this.clientesRepository = clientesRepository;
    //     this.eventoRepository = eventoRepository;
    //     this.discotecaRepository = discotecaRepository;
    // }

    @Transactional(readOnly = true)
    public ReservaDTO consultarReservaIndividualDTO(Integer id) {
        // Usamos el método con FETCH del repositorio directamente
        Reserva reserva = reservaRepository.findByIdWithEventoAndCliente(id).orElse(null);
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
        // Este método aún devuelve TODAS las reservas sin filtro.
        // Se usará si un "super-admin" necesita ver todo, o para depuración.
        // El endpoint principal para admins ahora usará el nuevo método filtrado.
        List<Reserva> reservas = reservaRepository.findAllWithEventoAndCliente(); // Usamos el método con FETCH
        System.out.println("DEBUG: Obteniendo Todas las Reservas (Sin filtro). Cantidad: " + reservas.size());
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
        // Este método ya estaba bien y usa el filtro por cliente
        List<Reserva> reservas = reservaRepository.findByClienteIdClienteWithEvento(idCliente);
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

    // --- NUEVO MÉTODO CRÍTICO: Obtener reservas para las discotecas administradas por un admin ---
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasParaDiscotecasDelAdmin(Integer adminId) {
        // 1. Obtener todas las discotecas asociadas a este adminId
        // Usamos findByAdministrador_IdAdmin porque así está en DiscotecaRepository
        List<Discoteca> discotecasDelAdmin = discotecaRepository.findByAdministrador_IdAdmin(adminId);

        if (discotecasDelAdmin.isEmpty()) {
            System.out.println("DEBUG: Admin ID: " + adminId + " no tiene discotecas asociadas. Retornando lista vacía de reservas.");
            return Collections.emptyList(); // Si el admin no tiene discotecas, no hay reservas que mostrar
        }

        // 2. Extraer los NITs (IDs) de esas discotecas
        List<Integer> discotecaNits = discotecasDelAdmin.stream()
                .map(Discoteca::getNit) // Usamos getNit() ya que NIT es el ID
                .collect(Collectors.toList());

        System.out.println("DEBUG: Admin ID: " + adminId + " tiene discotecas con NITs: " + discotecaNits);

        // 3. Obtener las reservas para esos NITs de discoteca
        // Usamos el nuevo método de ReservaRepository
        List<Reserva> reservas = reservaRepository.findByEventoDiscotecaNitIn(discotecaNits);
        System.out.println("DEBUG: Obteniendo Reservas para discotecas de Admin ID: " + adminId + ". Cantidad: " + reservas.size());

        return reservas.stream()
                .map(ReservaDTO::new)
                .collect(Collectors.toList());
    }

    // --- MÉTODO CLAVE: Registrar reserva iniciada por el propio cliente ---
    @Transactional
    public Reserva registrarReservaParaCliente(Integer idClienteToken, Reserva reserva) {
        Clientes cliente = clientesRepository.findById(idClienteToken)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID del token: " + idClienteToken));
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

        // Reemplazamos reservasDao.saveReserva por reservaRepository.save
        return reservaRepository.save(reserva);
    }

    // --- Método existente para registrar reserva (usado por ADMIN) ---
    @Transactional
    public Reserva registrarReserva(Reserva reserva) {
        if (reserva.getCliente() == null || reserva.getCliente().getUsuarioCliente() == null || reserva.getCliente().getUsuarioCliente().isBlank()) {
            throw new RuntimeException("El usuario del cliente es requerido para registrar una reserva.");
        }
        Clientes cliente = clientesRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
        }
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

        // Reemplazamos reservasDao.registrarReserva por reservaRepository.save
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Reserva reserva) {
        // Usamos findByIdWithEventoAndCliente para asegurar que el cliente y evento estén cargados
        Optional<Reserva> existingReservaOpt = reservaRepository.findByIdWithEventoAndCliente(reserva.getIdReserva());
        if (existingReservaOpt.isPresent()) {
            Reserva existingReserva = existingReservaOpt.get();

            if (reserva.getCliente() != null && reserva.getCliente().getUsuarioCliente() != null &&
                    !reserva.getCliente().getUsuarioCliente().isBlank() &&
                    // Comparamos el usuarioCliente, no el objeto completo
                    (existingReserva.getCliente() == null || !reserva.getCliente().getUsuarioCliente().equals(existingReserva.getCliente().getUsuarioCliente()))) {
                Clientes nuevoCliente = clientesRepository.findByUsuarioCliente(reserva.getCliente().getUsuarioCliente());
                if (nuevoCliente == null) {
                    throw new RuntimeException("Nuevo cliente no encontrado con usuario: " + reserva.getCliente().getUsuarioCliente());
                }
                existingReserva.setCliente(nuevoCliente);
            }
            // La lógica para cuando existingReserva.getCliente() es null y reserva.getCliente() no es null
            // ya está cubierta en la primera parte de la condición compuesta anterior.

            if (reserva.getFechaReserva() != null) existingReserva.setFechaReserva(reserva.getFechaReserva());
            if (reserva.getEstado() != null) existingReserva.setEstado(reserva.getEstado());
            if (reserva.getEstadoPago() != null) existingReserva.setEstadoPago(reserva.getEstadoPago());
            if (reserva.getCantidadTickets() != null) existingReserva.setCantidadTickets(reserva.getCantidadTickets());
            if (reserva.getIdTransaccion() != null) existingReserva.setIdTransaccion(reserva.getIdTransaccion());
            if (reserva.getMontoTotal() != null) existingReserva.setMontoTotal(reserva.getMontoTotal());
            if (reserva.getPreferenceId() != null) existingReserva.setPreferenceId(reserva.getPreferenceId());

            // Reemplazamos reservasDao.actualizarReserva por reservaRepository.save
            return reservaRepository.save(existingReserva);
        }
        return null;
    }

    @Transactional
    public Reserva actualizarEstadoPagoReserva(Integer idReserva, String nuevoEstadoPago) {
        // Reemplazamos reservasDao.actualizarEstadoPagoReserva por lógica directa con JpaRepository
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
        // Reemplazamos reservasDao.eliminarReserva por lógica directa con JpaRepository
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}