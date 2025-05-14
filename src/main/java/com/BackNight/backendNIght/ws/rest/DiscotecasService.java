package com.BackNight.backendNIght.ws.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.BackNight.backendNIght.ws.dao.DiscotecasDao;
import com.BackNight.backendNIght.ws.entity.Discoteca;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app") // Permite solicitudes solo desde el frontend
public class DiscotecasService {

    @Autowired
    private DiscotecasDao discotecasDao;

    // Obtener una discoteca específica por ID
    @GetMapping("discotecas/{id}")
    public ResponseEntity<Discoteca> getDiscoteca(@PathVariable String id) {
        Discoteca discoteca = discotecasDao.consultarDiscotecaIndividual(id);
        if (discoteca == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discoteca);
    }

    // Obtener la lista de todas las discotecas
    @GetMapping("discotecas-list")
    public ResponseEntity<List<Discoteca>> getDiscotecaList() {
        List<Discoteca> discotecas = discotecasDao.obtenerListaDiscotecas();
        return ResponseEntity.ok(discotecas);
    }

    // Registrar una nueva discoteca
    @PostMapping("guardar")
    public ResponseEntity<Discoteca> registrarDiscoteca(@RequestBody Discoteca discoteca) {
        Discoteca nuevaDiscoteca = discotecasDao.registrarDiscoteca(discoteca);
        return ResponseEntity.ok(nuevaDiscoteca);
    }

    // Actualizar una discoteca existente
    @PutMapping("actualizar")
    public ResponseEntity<Discoteca> actualizarDiscoteca(@RequestBody Discoteca discoteca) {
        Discoteca discotecaActualizada = discotecasDao.actualizarDiscoteca(discoteca);
        if (discotecaActualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discotecaActualizada);
    }

    // Eliminar una discoteca por ID
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminarDiscoteca(@PathVariable String id) {
        discotecasDao.eliminarDiscoteca(id);
        return ResponseEntity.ok().build();
    }
}
