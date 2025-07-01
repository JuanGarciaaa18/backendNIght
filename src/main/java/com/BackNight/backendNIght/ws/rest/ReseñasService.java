package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.ReseñasDao;
import com.BackNight.backendNIght.ws.entity.Reseña;
import com.BackNight.backendNIght.ws.util.JwtUtil; // Asume que tienes esta utilidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio/reseñas") // Un prefijo claro para los endpoints de reseñas
@CrossOrigin(origins = "http://localhost:5173") // Asegúrate de que sea tu frontend
public class ReseñasService {

    @Autowired
    private ReseñasDao reseñasDao;

    // Método de ayuda para extraer el ID del usuario (cliente) desde el token JWT
    // Asume que tu JWT tiene un "idUsuario" para clientes.
    private Integer getClienteIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                // Asegúrate de que JwtUtil.extractIdUsuarioFromToken pueda extraer el ID de cliente
                return JwtUtil.extractIdUsuarioFromToken(token);
            } catch (Exception e) {
                System.err.println("Error al extraer ID de cliente del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Endpoint para registrar una nueva reseña (privado, requiere token de cliente).
     * POST /servicio/reseñas/registrar
     * Body: { "puntuacion": 5, "comentario": "Gran experiencia!", "discoteca": { "nit": 1 } }
     */
    @PostMapping("/registrar")
    public ResponseEntity<Reseña> registrarReseña(@RequestBody Reseña reseña,
                                                  @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer idCliente = getClienteIdFromAuthHeader(authorizationHeader);
            if (idCliente == null) {
                System.out.println("Backend Reseñas: Acceso no autorizado para registrar reseña (401).");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (reseña.getDiscoteca() == null || reseña.getDiscoteca().getNit() == null) {
                System.out.println("Backend Reseñas: NIT de discoteca no proporcionado para la reseña (400).");
                return ResponseEntity.badRequest().body(null);
            }

            Reseña nuevaReseña = reseñasDao.registrarReseña(reseña, idCliente, reseña.getDiscoteca().getNit());
            // Opcional: Limpiar referencias para la respuesta si no quieres devolver el objeto completo
            nuevaReseña.setCliente(null);
            nuevaReseña.setDiscoteca(null);
            System.out.println("Backend Reseñas: Reseña registrada con ID " + nuevaReseña.getIdReseña() + " (201 Created).");
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReseña);
        } catch (RuntimeException e) {
            System.err.println("Backend Reseñas: Error al registrar reseña: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O 409 Conflict si es por duplicidad
        } catch (Exception e) {
            System.err.println("Backend Reseñas: Error interno al registrar reseña: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para obtener todas las reseñas de una discoteca (público).
     * GET /servicio/reseñas/discoteca/{nitDiscoteca}
     */
    @GetMapping("/discoteca/{nitDiscoteca}")
    public ResponseEntity<List<Reseña>> getReseñasPorDiscoteca(@PathVariable Integer nitDiscoteca) {
        try {
            List<Reseña> reseñas = reseñasDao.obtenerReseñasPorDiscoteca(nitDiscoteca);
            // Las referencias ya se limpian en el DAO, pero puedes hacer ajustes adicionales si es necesario
            System.out.println("Backend Reseñas: Reseñas para discoteca " + nitDiscoteca + " enviadas (200 OK). Cantidad: " + reseñas.size());
            return ResponseEntity.ok(reseñas);
        } catch (Exception e) {
            System.err.println("Backend Reseñas: Error interno al obtener reseñas por discoteca: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para obtener todas las reseñas de un cliente logueado (privado).
     * GET /servicio/reseñas/mis-reseñas
     */
    @GetMapping("/mis-reseñas")
    public ResponseEntity<List<Reseña>> getMisReseñas(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer idCliente = getClienteIdFromAuthHeader(authorizationHeader);
            if (idCliente == null) {
                System.out.println("Backend Reseñas: Acceso no autorizado para ver mis reseñas (401).");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Reseña> reseñas = reseñasDao.obtenerReseñasPorCliente(idCliente);
            System.out.println("Backend Reseñas: Reseñas para cliente " + idCliente + " enviadas (200 OK). Cantidad: " + reseñas.size());
            return ResponseEntity.ok(reseñas);
        } catch (Exception e) {
            System.err.println("Backend Reseñas: Error interno al obtener mis reseñas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para actualizar una reseña existente (privado, requiere token de cliente).
     * PUT /servicio/reseñas/actualizar
     * Body: { "idReseña": 1, "puntuacion": 4, "comentario": "Mejoró bastante el ambiente." }
     */
    @PutMapping("/actualizar")
    public ResponseEntity<Reseña> actualizarReseña(@RequestBody Reseña reseña,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        Integer idCliente = getClienteIdFromAuthHeader(authorizationHeader);
        if (idCliente == null) {
            System.out.println("Backend Reseñas: Acceso no autorizado para actualizar reseña (401).");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Reseña actualizada = reseñasDao.actualizarReseña(reseña, idCliente);
        if (actualizada == null) {
            System.out.println("Backend Reseñas: Reseña con ID " + reseña.getIdReseña() + " no encontrada o no autorizada para actualizar (404).");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // Opcional: Limpiar referencias para la respuesta
        actualizada.setCliente(null);
        actualizada.setDiscoteca(null);
        System.out.println("Backend Reseñas: Reseña " + actualizada.getIdReseña() + " actualizada por cliente " + idCliente + " (200 OK).");
        return ResponseEntity.ok(actualizada);
    }

    /**
     * Endpoint para eliminar una reseña (privado, requiere token de cliente).
     * DELETE /servicio/reseñas/eliminar/{id}
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarReseña(@PathVariable Integer id,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        Integer idCliente = getClienteIdFromAuthHeader(authorizationHeader);
        if (idCliente == null) {
            System.out.println("Backend Reseñas: Acceso no autorizado para eliminar reseña (401).");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean eliminada = reseñasDao.eliminarReseña(id, idCliente);
        if (!eliminada) {
            System.out.println("Backend Reseñas: Reseña con ID " + id + " no encontrada o no autorizada para eliminar (404).");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        System.out.println("Backend Reseñas: Reseña " + id + " eliminada por cliente " + idCliente + " (200 OK).");
        return ResponseEntity.ok().build();
    }
}