package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Importa esto

import java.util.List;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class EventosService {

    @Autowired
    private EventosDao eventosDao;

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
    @Transactional // Añade @Transactional para asegurar que la sesión de Hibernate esté abierta
    public ResponseEntity<Evento> getEventoPublico(@PathVariable Integer id) {
        try {
            System.out.println("Backend: Recibida solicitud PÚBLICA para evento con ID: " + id);
            Evento evento = eventosDao.consultarEventoIndividual(id);
            if (evento == null) {
                System.out.println("Backend: Evento con ID " + id + " no encontrado (404).");
                return ResponseEntity.notFound().build();
            }

            // AHORA AÑADE ESTA LÍNEA PARA INICIALIZAR LA DISCOTECA
            // Esto forzará la carga del proxy de Discoteca si está en LAZY
            if (evento.getDiscoteca() != null) {
                evento.getDiscoteca().getNit(); // Accede a cualquier propiedad de Discoteca (como getNit())
                // para forzar su inicialización mientras la transacción está activa.
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
            }
            evento.setAdministrador(null); // Esto también podría necesitar ser inicializado si lo necesitas en el JSON

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
    @Transactional // Añade @Transactional
    public ResponseEntity<List<Evento>> obtenerTodosEventosPublica() {
        try {
            List<Evento> eventos = eventosDao.obtenerTodosEventos();
            for (Evento evento : eventos) {
                // AÑADE ESTO PARA CADA EVENTO EN LA LISTA
                if (evento.getDiscoteca() != null) {
                    evento.getDiscoteca().getNit(); // Fuerza la inicialización
                    evento.getDiscoteca().setAdministrador(null);
                    evento.getDiscoteca().setZonas(null);
                }
                evento.setAdministrador(null); // Esto también
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
    @Transactional // Añade @Transactional
    public ResponseEntity<List<Evento>> getEventosByDiscotecaNit(@PathVariable Integer nitDiscoteca) {
        try {
            List<Evento> eventos = eventosDao.consultarEventosPorDiscotecaNit(nitDiscoteca);
            for (Evento evento : eventos) {
                // AÑADE ESTO PARA CADA EVENTO EN LA LISTA
                if (evento.getDiscoteca() != null) {
                    evento.getDiscoteca().getNit(); // Fuerza la inicialización
                    evento.getDiscoteca().setAdministrador(null);
                    evento.getDiscoteca().setZonas(null);
                }
                evento.setAdministrador(null); // Esto también
            }
            System.out.println("Backend: Eventos para discoteca NIT " + nitDiscoteca + " encontrados y enviados (200 OK). Cantidad: " + eventos.size());
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por discoteca NIT " + nitDiscoteca + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Los endpoints de admin no necesitan @Transactional si ya están dentro de un contexto transaccional
    // o si el DAO maneja las transacciones. Si el DAO no lo hace, también deberías añadir @Transactional.

    // ... (Mantén el resto de tus métodos sin cambios, o añade @Transactional si el DAO no lo provee)

    @GetMapping("/admin/eventos")
    @Transactional // Añade @Transactional si es necesario para asegurar la carga
    public ResponseEntity<List<Evento>> obtenerEventosPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                System.out.println("Backend: Acceso no autorizado para /admin/eventos (401).");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Evento> eventos = eventosDao.obtenerListaEventosPorAdmin(adminId);
            for (Evento evento : eventos) {
                if (evento.getDiscoteca() != null) {
                    evento.getDiscoteca().getNit(); // Fuerza la inicialización
                    // Limpieza adicional si se desea para el admin
                    // evento.getDiscoteca().setAdministrador(null);
                    // evento.getDiscoteca().setZonas(null);
                }
                // evento.setAdministrador(null); // Podrías querer enviar el admin para el admin
            }
            System.out.println("Backend: Eventos para admin " + adminId + " enviados (200 OK). Cantidad: " + eventos.size());
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ... (resto de tus métodos como registrarEvento, actualizarEvento, eliminarEvento)
    // Para registrar y actualizar, asegúrate de que el objeto Discoteca (y Administrador)
    // que viene en el @RequestBody sea completo o que lo busques en el DAO.

    @PostMapping("/guardar-evento")
    @Transactional // Asegura que la operación de persistencia esté dentro de una transacción
    public ResponseEntity<Evento> registrarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Importante: Si el evento que llega en el request solo tiene el ID/NIT de la discoteca,
            // el DAO debe buscar y asignar la entidad Discoteca completa antes de guardar.
            // Si el frontend envía el objeto Discoteca completo, asegúrate de que sea válido.
            Evento nuevo = eventosDao.registrarEvento(evento, adminId);

            // Una vez guardado, para asegurar la serialización si la respuesta incluye la discoteca completa
            if (nuevo.getDiscoteca() != null) {
                nuevo.getDiscoteca().getNit(); // Inicializa el proxy si aún no lo está
                nuevo.getDiscoteca().setAdministrador(null);
                nuevo.getDiscoteca().setZonas(null);
            }
            nuevo.setAdministrador(null);
            System.out.println("Backend: Evento " + nuevo.getIdEvento() + " guardado por admin " + adminId + " (201 Created).");
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            System.err.println("Backend: Error al guardar evento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}