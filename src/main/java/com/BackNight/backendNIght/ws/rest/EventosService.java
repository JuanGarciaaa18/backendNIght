package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class EventosService {

    @Autowired
    private EventosDao eventosDao;

    // Helper para obtener el ID del usuario (administrador) desde el token
    private Integer getUsuarioIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                return JwtUtil.extractIdUsuarioFromToken(token); // Usa el método genérico
            } catch (Exception e) {
                System.err.println("Error al extraer ID de usuario del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @GetMapping("/evento/{id}")
    public ResponseEntity<Evento> getEvento(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Evento evento = eventosDao.consultarEventoIndividual(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        // Verificar que el evento pertenezca al administrador logueado
        if (evento.getAdministrador() == null || !evento.getAdministrador().getIdAdmin().equals(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Opcional: limpiar referencias para evitar ciclos JSON
        evento.setAdministrador(null);
        if (evento.getDiscoteca() != null) {
            evento.getDiscoteca().setAdministrador(null);
            evento.getDiscoteca().setZonas(null);
        }
        return ResponseEntity.ok(evento);
    }

    // --- Endpoint PÚBLICO para obtener TODOS los eventos ---
    // No requiere Authorization header
    @GetMapping("/eventos-list") // Mantén esta ruta para el frontend público
    public ResponseEntity<List<Evento>> obtenerTodosEventosPublica() { // Cambiado el nombre del método
        try {
            List<Evento> eventos = eventosDao.obtenerTodosEventos(); // Llama al método que trae todos
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN) para obtener eventos por ADMIN logueado ---
    // Requiere Authorization header y filtra por idAdmin
    @GetMapping("/admin/eventos") // NUEVA RUTA para el panel de administrador
    public ResponseEntity<List<Evento>> obtenerEventosPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // No autorizado si no hay ID
            }
            // Filtrar eventos por el ID del administrador logueado
            List<Evento> eventos = eventosDao.obtenerListaEventosPorAdmin(adminId);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
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
            // Pasar el idAdmin al DAO para que asocie el evento
            Evento nuevo = eventosDao.registrarEvento(evento, adminId);
            // Opcional: limpiar referencias antes de enviar al frontend
            nuevo.setAdministrador(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
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
        // Pasar el idAdmin al DAO para la validación de propiedad
        Evento actualizado = eventosDao.actualizarEvento(evento, adminId);
        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // Opcional: limpiar referencias antes de enviar al frontend
        actualizado.setAdministrador(null);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Pasar el idAdmin al DAO para la validación de propiedad
        boolean eliminada = eventosDao.eliminarEvento(id, adminId);
        if (!eliminada) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }
}
