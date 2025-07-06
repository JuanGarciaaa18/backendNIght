package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import com.BackNight.backendNIght.ws.dto.EventoAdminDTO; // ¡Importa este!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // ¡Importa este!

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class EventosService {

    @Autowired
    private EventosDao eventosDao;

    @Autowired
    private AdministradoresRepository administradoresRepository;

    private Integer getUsuarioIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                return JwtUtil.extractIdUsuarioFromToken(token);
            } catch (Exception e) {
                System.err.println("Error al extraer ID de usuario del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    // --- Endpoint PÚBLICO: Obtener un evento individual por su ID ---
    @GetMapping("/evento/{id}")
    public ResponseEntity<EventoAdminDTO> getEventoPublico(@PathVariable Integer id) {
        try {
            Evento evento = eventosDao.consultarEventoIndividual(id);
            if (evento == null) {
                return ResponseEntity.notFound().build();
            }
            // Mapea la entidad a DTO antes de enviarla
            EventoAdminDTO eventoDTO = new EventoAdminDTO(evento);
            return ResponseEntity.ok(eventoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener TODOS los eventos ---
    @GetMapping("/eventos-list")
    public ResponseEntity<List<EventoAdminDTO>> obtenerTodosEventosPublica() {
        try {
            List<Evento> eventos = eventosDao.obtenerTodosEventos();
            // Mapea la lista de entidades a lista de DTOs
            List<EventoAdminDTO> eventoDTOs = eventos.stream()
                    .map(EventoAdminDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(eventoDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener eventos por NIT de discoteca ---
    @GetMapping("/eventos-por-discoteca/{nitDiscoteca}")
    public ResponseEntity<List<EventoAdminDTO>> getEventosByDiscotecaNit(@PathVariable Integer nitDiscoteca) {
        try {
            List<Evento> eventos = eventosDao.consultarEventosPorDiscotecaNit(nitDiscoteca);
            // Mapea la lista de entidades a lista de DTOs
            List<EventoAdminDTO> eventoDTOs = eventos.stream()
                    .map(EventoAdminDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(eventoDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Obtener eventos por ADMIN logueado ---
    @GetMapping("/admin/eventos")
    public ResponseEntity<List<EventoAdminDTO>> obtenerEventosPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Evento> eventos = eventosDao.obtenerListaEventosPorAdmin(adminId);
            // Mapea la lista de entidades a lista de DTOs
            List<EventoAdminDTO> eventoDTOs = eventos.stream()
                    .map(EventoAdminDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(eventoDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/guardar-evento")
    public ResponseEntity<EventoAdminDTO> registrarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<Administradores> optionalAdmin = administradoresRepository.findById(adminId);
            if (optionalAdmin.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            evento.setAdministrador(optionalAdmin.get());

            // ¡Importante! Si el frontend solo envía el NIT de la discoteca, DEBES cargar la entidad Discoteca completa
            // para que JPA pueda manejar la relación correctamente. Si el frontend envía el objeto completo de discoteca,
            // y es una entidad persistente, no es necesario. Pero para ser seguro:
            // @Autowired private DiscotecasRepository discotecasRepository; // Necesitarías inyectar esto
            // if (evento.getDiscoteca() != null && evento.getDiscoteca().getNit() != null) {
            //     discotecasRepository.findById(evento.getDiscoteca().getNit()).ifPresent(evento::setDiscoteca);
            // }


            Evento nuevo = eventosDao.registrarEvento(evento);
            // Mapea la entidad a DTO antes de enviarla
            EventoAdminDTO nuevoDTO = new EventoAdminDTO(nuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/actualizar-evento")
    public ResponseEntity<EventoAdminDTO> actualizarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Evento actualizado = eventosDao.actualizarEvento(evento, adminId);
        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // Mapea la entidad a DTO antes de enviarla
        EventoAdminDTO actualizadoDTO = new EventoAdminDTO(actualizado);
        return ResponseEntity.ok(actualizadoDTO);
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean eliminada = eventosDao.eliminarEvento(id, adminId);
        if (!eliminada) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }
}