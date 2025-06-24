package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.DiscotecasDao;
import com.BackNight.backendNIght.ws.entity.Discoteca;
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
public class DiscotecasService {

    @Autowired
    private DiscotecasDao discotecasDao;

    // Helper para obtener el ID del usuario (administrador o cliente) desde el token
    private Integer getUsuarioIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Eliminar "Bearer "
            try {
                return JwtUtil.extractIdUsuarioFromToken(token); // Usa el método genérico
            } catch (Exception e) {
                System.err.println("Error al extraer ID de usuario del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @GetMapping("/discoteca/{nit}")
    public ResponseEntity<Discoteca> getDiscoteca(@PathVariable Integer nit, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Discoteca discoteca = discotecasDao.consultarDiscotecaIndividual(nit);
        if (discoteca == null) {
            return ResponseEntity.notFound().build();
        }
        // Verificar que la discoteca pertenezca al administrador logueado
        if (discoteca.getAdministrador() == null || !discoteca.getAdministrador().getIdAdmin().equals(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Acceso denegado
        }
        // Opcional: limpiar la referencia al administrador para evitar ciclos JSON si no es necesario en el frontend
        discoteca.setAdministrador(null);
        if (discoteca.getZonas() != null) {
            discoteca.getZonas().forEach(z -> {
                z.setDiscoteca(null);
                if (z.getMesas() != null) {
                    z.getMesas().forEach(m -> m.setZona(null));
                }
            });
        }
        return ResponseEntity.ok(discoteca);
    }

    // --- Endpoint PÚBLICO para obtener TODAS las discotecas ---
    // No requiere Authorization header
    @GetMapping("/discotecas-list") // Mantén esta ruta para el frontend público
    public ResponseEntity<List<Discoteca>> obtenerTodasDiscotecasPublica() {
        try {
            List<Discoteca> discotecas = discotecasDao.obtenerTodasDiscotecas(); // Llama al método que trae todas
            return ResponseEntity.ok(discotecas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Endpoint PRIVADO (ADMIN) para obtener discotecas por ADMIN logueado ---
    // Requiere Authorization header y filtra por idAdmin
    @GetMapping("/admin/discotecas") // NUEVA RUTA para el panel de administrador
    public ResponseEntity<List<Discoteca>> obtenerDiscotecasPorAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // No autorizado si no hay ID
            }
            // Filtrar discotecas por el ID del administrador logueado
            List<Discoteca> discotecas = discotecasDao.obtenerListaDiscotecasPorAdmin(adminId);
            return ResponseEntity.ok(discotecas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/guardar")
    public ResponseEntity<Discoteca> registrarDiscoteca(@RequestBody Discoteca discoteca, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Pasar el idAdmin al DAO para que asocie la discoteca
            Discoteca nuevaDiscoteca = discotecasDao.registrarDiscoteca(discoteca, adminId);
            // Opcional: limpiar la referencia al administrador antes de enviar al frontend
            nuevaDiscoteca.setAdministrador(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDiscoteca);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/actualizar/{nit}")
    public ResponseEntity<Discoteca> actualizarDiscoteca(@PathVariable Integer nit, @RequestBody Discoteca discoteca, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        discoteca.setNit(nit); // Asegura que el NIT de la discoteca sea el del path
        // Pasar el idAdmin al DAO para la validación de propiedad
        Discoteca discotecaActualizada = discotecasDao.actualizarDiscoteca(discoteca, adminId);
        if (discotecaActualizada == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // Opcional: limpiar la referencia al administrador antes de enviar al frontend
        discotecaActualizada.setAdministrador(null);
        return ResponseEntity.ok(discotecaActualizada);
    }

    @DeleteMapping("/eliminar/{nit}")
    public ResponseEntity<Void> eliminarDiscoteca(@PathVariable Integer nit, @RequestHeader("Authorization") String authorizationHeader) {
        Integer adminId = getUsuarioIdFromAuthHeader(authorizationHeader);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Pasar el idAdmin al DAO para la validación de propiedad
        boolean eliminada = discotecasDao.eliminarDiscoteca(nit, adminId);
        if (!eliminada) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }
}
