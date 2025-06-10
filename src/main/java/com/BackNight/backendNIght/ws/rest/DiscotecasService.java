package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.DiscotecasDao;
import com.BackNight.backendNIght.ws.entity.Discoteca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class DiscotecasService {

    @Autowired
    private DiscotecasDao discotecasDao;

    @GetMapping("/discoteca/{id}")
    public ResponseEntity<Discoteca> getDiscoteca(@PathVariable Integer id) {
        Discoteca discoteca = discotecasDao.consultarDiscotecaIndividual(id);
        if (discoteca == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discoteca);
    }

    @GetMapping("/discotecas-list")
    public ResponseEntity<List<Discoteca>> obtenerListaDiscotecas() {
        try {
            List<Discoteca> discotecas = discotecasDao.obtenerListaDiscotecas();
            return ResponseEntity.ok(discotecas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Recibe JSON con imagen Base64 en el campo 'imagen'
    @PostMapping("/guardar")
    public ResponseEntity<Discoteca> registrarDiscoteca(@RequestBody Discoteca discoteca) {
        try {
            Discoteca nuevaDiscoteca = discotecasDao.registrarDiscoteca(discoteca);
            return ResponseEntity.ok(nuevaDiscoteca);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/actualizar/{nit}")
    public ResponseEntity<Discoteca> actualizarDiscoteca(@PathVariable Integer nit, @RequestBody Discoteca discoteca) {
        discoteca.setNit(nit); // Establece el NIT recibido como identificador
        Discoteca discotecaActualizada = discotecasDao.actualizarDiscoteca(discoteca);
        if (discotecaActualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discotecaActualizada);
    }



    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarDiscoteca(@PathVariable Integer id) {
        discotecasDao.eliminarDiscoteca(id);
        return ResponseEntity.ok().build();
    }
}
