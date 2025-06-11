package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class EventosService {

    @Autowired
    private EventosDao eventosDao;

    @GetMapping("/evento/{id}")
    public ResponseEntity<Evento> getEvento(@PathVariable Integer id) {
        Evento evento = eventosDao.consultarEventoIndividual(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evento);
    }

    @GetMapping("/eventos-list")
    public ResponseEntity<List<Evento>> obtenerListaEventos() {
        try {
            return ResponseEntity.ok(eventosDao.obtenerListaEventos());
        } catch (Exception e) {
            e.printStackTrace(); // 👈 Muestra el error exacto en consola
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/guardar-evento")
    public ResponseEntity<Evento> registrarEvento(@RequestBody Evento evento) {
        Evento nuevo = eventosDao.registrarEvento(evento);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/actualizar-evento")
    public ResponseEntity<Evento> actualizarEvento(@RequestBody Evento evento) {
        Evento actualizado = eventosDao.actualizarEvento(evento);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id) {
        eventosDao.eliminarEvento(id);
        return ResponseEntity.ok().build();
    }
}
