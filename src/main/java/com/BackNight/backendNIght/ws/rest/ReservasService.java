// src/main/java/com/BackNight/backendNIght/ws/rest/ReservasService.java
package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.service.ReservaService; // CAMBIO: Importa el nuevo servicio
import com.BackNight.backendNIght.ws.entity.Reserva; // Sigue siendo necesaria si los métodos de modificación devuelven entidades
import com.BackNight.backendNIght.ws.dto.ReservaDTO; // AÑADIDO: Importa el DTO
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ReservasService { // Este es tu controlador REST

    @Autowired
    private ReservaService reservaService; // CAMBIO: Inyecta el nuevo Servicio, no el DAO directamente

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

    // --- Endpoint PRIVADO (ADMIN): Obtener TODAS las reservas (Ahora devuelve DTOs) ---
    @GetMapping("/admin/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas(@RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<ReservaDTO> reservas = reservaService.obtenerTodasLasReservasDTO(); // CAMBIO: Llama al servicio para DTOs
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Registrar una nueva reserva ---
    // Este endpoint sigue recibiendo y devolviendo la entidad, lo cual está bien si es el flujo deseado
    @PostMapping("/guardar-reserva")
    public ResponseEntity<Reserva> registrarReserva(@RequestBody Reserva reserva, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Reserva nuevaReserva = reservaService.registrarReserva(reserva); // Llama al servicio
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
            Reserva actualizada = reservaService.actualizarReserva(reserva); // Llama al servicio
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

    // --- NUEVO ENDPOINT PRIVADO (ADMIN): Actualizar el estado de pago de una reserva ---
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
            Reserva updatedReserva = reservaService.actualizarEstadoPagoReserva(id, request.getEstadoPago()); // Llama al servicio
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
            boolean eliminada = reservaService.eliminarReserva(id); // Llama al servicio
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
    // Este endpoint también devolverá un DTO ahora
    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaDTO> getReservaPublico(@PathVariable Integer id) {
        try {
            ReservaDTO reserva = reservaService.consultarReservaIndividualDTO(id); // Llama al servicio para DTO
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