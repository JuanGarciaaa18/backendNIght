package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao; // Asume que este DAO existe
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.entity.Discoteca; // Importa Discoteca
import com.BackNight.backendNIght.ws.entity.Administradores; // Importa Administradores
import com.BackNight.backendNIght.ws.util.JwtUtil; // Asume que esta clase existe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Importa esta anotación

import java.util.List;
import java.util.stream.Collectors; // Para limpiar relaciones si es necesario en una lista

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app") // Ajusta esto si tu frontend está en otra URL
public class EventosService {

    @Autowired
    private EventosDao eventosDao; // Inyecta tu DAO

    // Método de ayuda para extraer el ID del usuario (administrador) desde el token JWT
    private Integer getUsuarioIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                return JwtUtil.extractIdUsuarioFromToken(token); // Asume que JwtUtil tiene este método
            } catch (Exception e) {
                System.err.println("Error al extraer ID de usuario del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    // Método de ayuda para inicializar relaciones LAZY y limpiar referencias para JSON
    private Evento initializeAndCleanEvent(Evento evento) {
        if (evento == null) {
            return null;
        }

        // Forzar la inicialización del proxy de Discoteca (si es LAZY)
        if (evento.getDiscoteca() != null) {
            Discoteca discoteca = evento.getDiscoteca();
            discoteca.getNit(); // Accede a una propiedad para forzar la carga
            // Limpiar referencias para evitar ciclos JSON y exponer solo lo necesario
            discoteca.setAdministrador(null); // No queremos el admin de la discoteca dentro del evento JSON
            discoteca.setEventos(null); // Evitar ciclos si Discoteca también tiene lista de Eventos
            discoteca.setZonas(null); // Si no necesitas zonas de la discoteca en el JSON del evento
        }

        // Limpiar referencia al Administrador del Evento si no es necesario en el JSON público
        evento.setAdministrador(null);

        // Limpiar referencias a Reservas si no necesitas que aparezcan anidadas en el JSON del evento
        evento.setReservas(null);

        return evento;
    }

    // --- Endpoint PÚBLICO: Obtener un evento individual por su ID ---
    @GetMapping("/evento/{id}")
    @Transactional // Asegura que la sesión de Hibernate esté abierta para cargar los proxies
    public ResponseEntity<Evento> getEventoPublico(@PathVariable Integer id) {
        try {
            System.out.println("Backend: Recibida solicitud PÚBLICA para evento con ID: " + id);
            Evento evento = eventosDao.consultarEventoIndividual(id);
            if (evento == null) {
                System.out.println("Backend: Evento con ID " + id + " no encontrado (404).");
                return ResponseEntity.notFound().build();
            }

            Evento cleanedEvento = initializeAndCleanEvent(evento);
            System.out.println("Backend: Evento con ID " + id + " encontrado y enviado (200 OK).");
            return ResponseEntity.ok(cleanedEvento);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al cargar evento con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener TODOS los eventos ---
    @GetMapping("/eventos-list")
    @Transactional // Asegura que la sesión de Hibernate esté abierta
    public ResponseEntity<List<Evento>> obtenerTodosEventosPublica() {
        try {
            List<Evento> eventos = eventosDao.obtenerTodosEventos();
            List<Evento> cleanedEventos = eventos.stream()
                    .map(this::initializeAndCleanEvent)
                    .collect(Collectors.toList());
            System.out.println("Backend: Lista de todos los eventos enviada (200 OK). Cantidad: " + cleanedEventos.size());
            return ResponseEntity.ok(cleanedEventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener todos los eventos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PÚBLICO: Obtener eventos por NIT de discoteca ---
    @GetMapping("/eventos-por-discoteca/{nitDiscoteca}")
    @Transactional // Asegura que la sesión de Hibernate esté abierta
    public ResponseEntity<List<Evento>> getEventosByDiscotecaNit(@PathVariable Integer nitDiscoteca) {
        try {
            List<Evento> eventos = eventosDao.consultarEventosPorDiscotecaNit(nitDiscoteca);
            List<Evento> cleanedEventos = eventos.stream()
                    .map(this::initializeAndCleanEvent)
                    .collect(Collectors.toList());
            System.out.println("Backend: Eventos para discoteca NIT " + nitDiscoteca + " encontrados y enviados (200 OK). Cantidad: " + cleanedEventos.size());
            return ResponseEntity.ok(cleanedEventos);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por discoteca NIT " + nitDiscoteca + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Obtener eventos por ADMIN logueado ---
    @GetMapping("/admin/eventos")
    @Transactional // Asegura que la sesión de Hibernate esté abierta para cargar los proxies
    public ResponseEntity<List<Evento>> obtenerEventosPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                System.out.println("Backend: Acceso no autorizado para /admin/eventos (401).");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Evento> eventos = eventosDao.obtenerListaEventosPorAdmin(adminId);
            // Para los endpoints de ADMIN, podrías decidir si quieres limpiar estas referencias o no.
            // Si el frontend del admin necesita ver el administrador o las zonas de la discoteca,
            // ajusta la lógica de limpieza aquí.
            List<Evento> processedEvents = eventos.stream().map(evento -> {
                if (evento.getDiscoteca() != null) {
                    Discoteca discoteca = evento.getDiscoteca();
                    discoteca.getNit(); // Forzar carga
                    // Mantener el administrador de la discoteca para el admin si es necesario
                    // discoteca.setAdministrador(null);
                    discoteca.setEventos(null); // Evitar ciclos
                    discoteca.setZonas(null); // Si no necesita las zonas de la discoteca
                }
                // Mantener el administrador del evento para el admin si es necesario
                // evento.setAdministrador(null);
                evento.setReservas(null); // Si no necesitas reservas en esta vista para el admin
                return evento;
            }).collect(Collectors.toList());

            System.out.println("Backend: Eventos para admin " + adminId + " enviados (200 OK). Cantidad: " + processedEvents.size());
            return ResponseEntity.ok(processedEvents);
        } catch (Exception e) {
            System.err.println("Backend: Error interno al obtener eventos por admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Registrar un nuevo evento ---
    @PostMapping("/guardar-evento")
    @Transactional // Asegura que la operación de persistencia esté dentro de una transacción
    public ResponseEntity<Evento> registrarEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Importante: El DAO (EventosDao) debe asegurarse de que las entidades
            // Discoteca y Administradores asociadas al evento estén "managed" por JPA
            // (es decir, que sean referencias a objetos existentes en la DB, no nuevos objetos solo con ID).
            Evento nuevoEvento = eventosDao.registrarEvento(evento, adminId);

            // Inicializar y limpiar el objeto para la respuesta, similar a los GET
            Evento cleanedNuevoEvento = initializeAndCleanEvent(nuevoEvento);
            System.out.println("Backend: Evento " + cleanedNuevoEvento.getIdEvento() + " guardado por admin " + adminId + " (201 Created).");
            return ResponseEntity.status(HttpStatus.CREATED).body(cleanedNuevoEvento);
        } catch (Exception e) {
            System.err.println("Backend: Error al guardar evento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Actualizar un evento existente ---
    @PutMapping("/actualizar-evento")
    @Transactional // Asegura que la operación de persistencia esté dentro de una transacción
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
        // Inicializar y limpiar el objeto para la respuesta
        Evento cleanedActualizado = initializeAndCleanEvent(actualizado);
        System.out.println("Backend: Evento " + cleanedActualizado.getIdEvento() + " actualizado por admin " + adminId + " (200 OK).");
        return ResponseEntity.ok(cleanedActualizado);
    }

    // --- Endpoint PRIVADO (ADMIN): Eliminar un evento ---
    @DeleteMapping("/eliminar-evento/{id}")
    @Transactional // Asegura que la operación de persistencia esté dentro de una transacción
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