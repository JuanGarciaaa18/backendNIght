package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.ReservasDao;
import com.BackNight.backendNIght.ws.entity.Reserva;
import com.BackNight.backendNIght.ws.util.JwtUtil; // Asegúrate de tener tu clase JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173") // ¡Asegúrate que este puerto sea el de tu frontend!
public class ReservasService {

    @Autowired
    private ReservasDao reservasDao;

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

    // --- Endpoint PRIVADO (ADMIN): Obtener TODAS las reservas ---
    // ¡CORREGIDO AQUÍ! La ruta ahora coincide con tu frontend
    @GetMapping("/admin/reservas") // <--- CAMBIO IMPORTANTE: Ahora es /admin/reservas
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas(@RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<Reserva> reservas = reservasDao.obtenerTodasReservas();
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
            Reserva nuevaReserva = reservasDao.registrarReserva(reserva);
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
            Reserva actualizada = reservasDao.actualizarReserva(reserva);
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
    // Ahora espera un JSON con la propiedad "estadoPago"
    @PutMapping("/actualizar-estado-pago-reserva/{id}")
    public ResponseEntity<Reserva> actualizarEstadoPagoReserva(
            @PathVariable Integer id,
            @RequestBody EstadoPagoRequest request, // <- Usa el DTO interno para el RequestBody
            @RequestHeader("Authorization") String authorizationHeader) {

        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Se asume que request.getEstadoPago() no será nulo por la validación del JSON
            Reserva updatedReserva = reservasDao.actualizarEstadoPagoReserva(id, request.getEstadoPago());
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
    // El frontend debe enviar un JSON como {"estadoPago": "Pagado"}
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
            boolean eliminada = reservasDao.eliminarReserva(id);
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
    public ResponseEntity<Reserva> getReservaPublico(@PathVariable Integer id) {
        try {
            Reserva reserva = reservasDao.consultarReservaIndividual(id);
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