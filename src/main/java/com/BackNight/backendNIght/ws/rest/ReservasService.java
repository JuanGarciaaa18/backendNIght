// src/main/java/com/BackNight/backendNIght/ws/rest/ReservasService.java
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
@CrossOrigin(origins = "http://localhost:5173")
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
    @GetMapping("/admin/reservas")
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
    // Puedes enviarlo como un JSON: {"estadoPago": "PAGADO"}
    @PutMapping("/actualizar-estado-pago-reserva/{id}")
    public ResponseEntity<Reserva> actualizarEstadoPagoReserva(
            @PathVariable Integer id,
            @RequestBody String nuevoEstadoPago, // Espera un string simple (ej. "PAGADO") o un JSON {"estadoPago": "PAGADO"}
            @RequestHeader("Authorization") String authorizationHeader) {

        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Si envías un JSON como {"estadoPago": "PAGADO"}, necesitarás extraer el valor
        // o si envías un string plano, puedes usar @RequestBody String.
        // Para simplificar, asumiremos que se envía un JSON {"estadoPago": "VALOR"}.
        // Si usas @RequestBody String, el cuerpo debe ser SOLO el string "PAGADO".
        // Vamos a reajustar para que espere un objeto con la propiedad.
        // O mejor, un simple request param si es solo un estado.

        // Opción 1: Recibir un JSON con la propiedad
        // public ResponseEntity<Reserva> actualizarEstadoPagoReserva(
        // @PathVariable Integer id, @RequestBody EstadoPagoRequest request, ... )
        // static class EstadoPagoRequest { public String estadoPago; }

        // Opción 2: Recibir el string directamente en el body (más simple para un solo valor)
        // Pero el frontend tiende a enviar JSONs. Mejor un objeto simple.
        // Vamos con la opción 1 con un mapa simple.

        try {
            // Asumimos que el frontend envía un JSON como {"estadoPago": "PAGADO"}
            // Spring puede mapear automáticamente un String a un Map si el JSON es simple.
            // Para simplificar aún más y evitar la necesidad de una clase DTO,
            // si solo se envía un STRING en el body (ej: "PAGADO"), se podría usar:
            // @RequestBody String estadoPagoPayload

            // Sin embargo, si quieres enviar un JSON, lo más robusto es un DTO.
            // Para esta demostración y facilidad, asumiremos que se envía un String "PAGADO"
            // O mejor aún, pasarlo como @RequestParam, pero si es un PUT, @RequestBody es más idiomático.

            // Para que el @RequestBody String funcione, el cliente debe enviar el body como
            // texto plano "PAGADO" (sin {})
            Reserva updatedReserva = reservasDao.actualizarEstadoPagoReserva(id, nuevoEstadoPago); // nuevoEstadoPago es el string directamente
            if (updatedReserva == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(updatedReserva);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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