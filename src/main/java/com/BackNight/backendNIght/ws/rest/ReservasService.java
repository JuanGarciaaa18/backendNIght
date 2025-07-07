// src/main/java/com/BackNight/backendNIght/ws/rest/ReservasService.java
package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.service.ReservaService;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.dto.ReservaDTO;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio") // Este es el prefijo base para todos los endpoints en este controlador
// Si ya tienes una configuración global de CORS (CorsConfig.java), puedes eliminar esta línea:
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ReservasService {

    @Autowired
    private ReservaService reservaService;

    // Método auxiliar para extraer el ID del usuario (cliente o admin) del token JWT
    private Integer getUsuarioIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Elimina "Bearer "
            try {
                // Asegúrate de que JwtUtil.extractIdUsuarioFromToken puede devolver el ID del CLIENTE
                return JwtUtil.extractIdUsuarioFromToken(token);
            } catch (Exception e) {
                System.err.println("Error al extraer ID de usuario del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    // --- ESTE ES EL ENDPOINT QUE NECESITAS AÑADIR/VERIFICAR ---
    // La ruta completa que tu frontend está buscando es: /servicio/cliente/mis-reservas
    @GetMapping("/cliente/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> getMisReservas(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer idCliente = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (idCliente == null) {
                // Si el token es inválido o no se puede extraer el ID del cliente
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Llama al servicio de reservas para obtener las reservas de este cliente
            List<ReservaDTO> reservas = reservaService.obtenerMisReservasDTO(idCliente);

            // Devuelve la lista de reservas (puede ser vacía si no hay ninguna)
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            e.printStackTrace(); // Usa un logger en producción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Obtener TODAS las reservas (Ahora devuelve DTOs) ---
    @GetMapping("/admin/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas(@RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<ReservaDTO> reservas = reservaService.obtenerTodasLasReservasDTO();
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Registrar una nueva reserva ---
    @PostMapping("/guardar-reserva")
    public ResponseEntity<Reserva> registrarReserva(@RequestBody Reserva reserva, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Reserva nuevaReserva = reservaService.registrarReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (RuntimeException e) {
            System.err.println("Error al registrar reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Actualizar una reserva existente ---
    @PutMapping("/actualizar-reserva")
    public ResponseEntity<Reserva> actualizarReserva(@RequestBody Reserva reserva, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Reserva actualizada = reservaService.actualizarReserva(reserva);
            if (actualizada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Actualizar el estado de pago de una reserva ---
    @PutMapping("/actualizar-estado-pago-reserva/{id}")
    public ResponseEntity<Reserva> actualizarEstadoPagoReserva(
            @PathVariable Integer id,
            @RequestBody EstadoPagoRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Reserva updatedReserva = reservaService.actualizarEstadoPagoReserva(id, request.getEstadoPago());
            if (updatedReserva == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(updatedReserva);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Clase DTO interna para el RequestBody de actualizarEstadoPagoReserva
    static class EstadoPagoRequest {
        private String estadoPago;

        public String getEstadoPago() {
            return estadoPago;
        }

        public void setEstadoPago(String estadoPago) {
            this.estadoPago = estadoPago;
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Eliminar una reserva ---
    @DeleteMapping("/eliminar-reserva/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            boolean eliminada = reservaService.eliminarReserva(id);
            if (!eliminada) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Opcional: Endpoint Público para obtener una reserva individual (si es necesario) ---
    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaDTO> getReservaPublico(@PathVariable Integer id) {
        try {
            ReservaDTO reserva = reservaService.consultarReservaIndividualDTO(id);
            if (reserva == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}