package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app") // Ajusta esto si tu frontend está en otra URL
public class EventosService {

    @Autowired
    private EventosDao eventosDao;

    @Autowired
    private AdministradoresRepository administradoresRepository;

    // Método de ayuda para extraer el ID del usuario (administrador) desde el token JWT
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
    public ResponseEntity<Evento> getEventoPublico(@PathVariable Integer id) {
        try {
            System.out.println("Backend: Recibida solicitud PÚBLICA para evento con ID: " + id);
            Evento evento = eventosDao.consultarEventoIndividual(id);
            if (evento == null) {
                System.out.println("Backend: Evento con ID " + id + " no encontrado (404).");
                return ResponseEntity.notFound().build();
            }
            evento.setAdministrador(null); // Limpiar para evitar ciclos JSON
            if (evento.getDiscoteca() != null) {
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
                evento.getDiscoteca().setEventos(null); // ¡Añadido: Limpiar la lista de eventos de la discoteca!
            }
            System.out.println("Backend: Evento con ID " + id + " encontrado y enviado (200 OK).");
            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al cargar evento con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener TODOS los eventos ---
    @GetMapping("/eventos-list")
    public ResponseEntity<List<Evento>> obtenerTodosEventosPublica() {
        try {
            List<Evento> eventos = eventosDao.obtenerTodosEventos();
            for (Evento evento : eventos) {
                evento.setAdministrador(null);
                if (evento.getDiscoteca() != null) {
                    evento.getDiscoteca().setAdministrador(null);
                    evento.getDiscoteca().setZonas(null);
                    evento.getDiscoteca().setEventos(null); // ¡Añadido: Limpiar la lista de eventos de la discoteca!
                }
            }
            System.out.println("Backend: Lista de todos los eventos enviada (200 OK). Cantidad: " + eventos.size());
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener todos los eventos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener eventos por NIT de discoteca ---
    @GetMapping("/eventos-por-discoteca/{nitDiscoteca}")
    public ResponseEntity<List<Evento>> getEventosByDiscotecaNit(@PathVariable Integer nitDiscoteca) {
        try {
            List<Evento> eventos = eventosDao.consultarEventosPorDiscotecaNit(nitDiscoteca);
            for (Evento evento : eventos) {
                evento.setAdministrador(null);
                if (evento.getDiscoteca() != null) {
                    evento.getDiscoteca().setAdministrador(null);
                    evento.getDiscoteca().setZonas(null);
                    evento.getDiscoteca().setEventos(null); // ¡Añadido: Limpiar la lista de eventos de la discoteca!
                }
            }
            System.out.println("Backend: Eventos para discoteca NIT " + nitDiscoteca + " encontrados y enviados (200 OK). Cantidad: " + eventos.size());
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por discoteca NIT " + nitDiscoteca + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Obtener eventos por ADMIN logueado ---
    @GetMapping("/admin/eventos")
    public ResponseEntity<List<Evento>> obtenerEventosPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                System.out.println("Backend: Acceso no autorizado para /admin/eventos (401).");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Evento> eventos = eventosDao.obtenerListaEventosPorAdmin(adminId);
            // La limpieza de referencias ya se hace en el DAO para estos métodos
            System.out.println("Backend: Eventos para admin " + adminId + " enviados (200 OK). Cantidad: " + eventos.size());
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/guardar-evento")
    public ResponseEntity<Evento> registrarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // --- CRÍTICO: Cargar y asignar el objeto Administradores antes de pasar el Evento al DAO ---
            Optional<Administradores> optionalAdmin = administradoresRepository.findById(adminId);
            if (optionalAdmin.isEmpty()) {
                System.err.println("Backend: Administrador con ID " + adminId + " no encontrado para guardar evento.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            evento.setAdministrador(optionalAdmin.get()); // ¡Esto asegura que el objeto Administradores completo esté asociado!

            // Si el evento tiene una discoteca, asegúrate de que sea una entidad gestionada
            if (evento.getDiscoteca() != null && evento.getDiscoteca().getNit() != null) {
                // Si solo viene el NIT de la discoteca, necesitas cargar la entidad completa
                // Esto es importante si el frontend solo envía el NIT y no el objeto completo de Discoteca
                // Necesitarías inyectar DiscotecasRepository aquí si no lo tienes
                // @Autowired private DiscotecasRepository discotecasRepository;
                // Optional<Discoteca> optionalDiscoteca = discotecasRepository.findById(evento.getDiscoteca().getNit());
                // if (optionalDiscoteca.isPresent()) {
                //     evento.setDiscoteca(optionalDiscoteca.get());
                // } else {
                //     System.err.println("Backend: Discoteca con NIT " + evento.getDiscoteca().getNit() + " no encontrada para asociar al evento.");
                //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                // }
            }


            // Ahora, pasamos el objeto Evento ya completo al DAO
            Evento nuevo = eventosDao.registrarEvento(evento);

            // Opcional: Limpiar referencia de administrador para la respuesta JSON, si no la necesitas en el frontend
            nuevo.setAdministrador(null);
            if (nuevo.getDiscoteca() != null) {
                nuevo.getDiscoteca().setAdministrador(null);
                nuevo.getDiscoteca().setZonas(null);
                nuevo.getDiscoteca().setEventos(null); // ¡Añadido!
            }
            System.out.println("Backend: Evento " + nuevo.getIdEvento() + " guardado por admin " + adminId + " (201 Created).");
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            System.err.println("Backend: Error al guardar evento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/actualizar-evento")
    public ResponseEntity<Evento> actualizarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Evento actualizado = eventosDao.actualizarEvento(evento, adminId);
        if (actualizado == null) {
            System.out.println("Backend: Evento " + evento.getIdEvento() + " no encontrado o no autorizado para actualizar (404).");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        actualizado.setAdministrador(null);
        if (actualizado.getDiscoteca() != null) {
            actualizado.getDiscoteca().setAdministrador(null);
            actualizado.getDiscoteca().setZonas(null);
            actualizado.getDiscoteca().setEventos(null); // ¡Añadido!
        }
        System.out.println("Backend: Evento " + actualizado.getIdEvento() + " actualizado por admin " + adminId + " (200 OK).");
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean eliminada = eventosDao.eliminarEvento(id, adminId);
        if (!eliminada) {
            System.out.println("Backend: Evento " + id + " no encontrado o no autorizado para eliminar (404).");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        System.out.println("Backend: Evento " + id + " eliminado por admin " + adminId + " (200 OK).");
        return ResponseEntity.ok().build();
    }
}