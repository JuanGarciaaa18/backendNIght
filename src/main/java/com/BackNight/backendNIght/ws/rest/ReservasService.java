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
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ReservasService {

    @Autowired
    private ReservaService reservaService;

    // Método auxiliar para extraer el ID del usuario (cliente o admin) del token JWT
    // Asumimos que JwtUtil.extractIdUsuarioFromToken devuelve el ID correcto
    // (idCliente para clientes, idAdmin para administradores, según el rol).
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

    // --- Endpoint para que el CLIENTE haga su propia reserva ---
    @PostMapping("/cliente/reservar")
    public ResponseEntity<?> registrarReservaParaCliente(@RequestBody Reserva reserva, @RequestHeader("Authorization") String authorizationHeader) {
        Integer idCliente = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado: Token inválido o ausente.");
        }
        try {
            Reserva nuevaReserva = reservaService.registrarReservaParaCliente(idCliente, reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (RuntimeException e) {
            System.err.println("Error al registrar reserva para cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar la reserva.");
        }
    }

    // --- Endpoint para obtener las reservas del cliente autenticado ---
    @GetMapping("/cliente/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> getMisReservas(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer idCliente = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (idCliente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<ReservaDTO> reservas = reservaService.obtenerMisReservasDTO(idCliente);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Obtener reservas de sus discotecas ---
    // ¡ESTE ES EL CAMBIO CLAVE PARA EL FILTRADO DEL ADMIN!
    @GetMapping("/admin/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasAdminFiltradas(@RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // Llama al nuevo método del servicio que filtra por las discotecas del admin
            List<ReservaDTO> reservas = reservaService.obtenerReservasParaDiscotecasDelAdmin(adminId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- Endpoint PRIVADO (ADMIN): Registrar una nueva reserva (usando usuarioCliente) ---
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
            System.err.println("Error al registrar reserva (ADMIN): " + e.getMessage());
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