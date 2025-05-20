package com.BackNight.backendNIght.ws.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.BackNight.backendNIght.ws.dao.EventosDao;
import com.BackNight.backendNIght.ws.entity.Eventos;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class EventosService {

    @Autowired
    private EventosDao eventosDao;


    @GetMapping("eventos/{id}")
    public ResponseEntity<Eventos> getEvento(@PathVariable String id_event) {
        Eventos evento = eventosDao.consultarEventoIndividual(id_event);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evento);
    }

    @GetMapping("eventos-list")
    public ResponseEntity<List<Eventos>> getEventosList() {
        List<Eventos> eventos = eventosDao.obtenerListaEventos();
        return ResponseEntity.ok(eventos) ;
    }



    @PostMapping("guardar")
    public ResponseEntity<Eventos> registrarEvento(@RequestBody Eventos eventos) {
        Eventos nuevoEvento = eventosDao.registrarEvento(eventos);
        return ResponseEntity.ok(nuevoEvento);
    }


    @PutMapping("actualizar")
    public ResponseEntity<Eventos> actualizarEvento(@RequestBody Eventos eventos) {
        Eventos eventoActualizado = eventosDao.actualizarEvento(eventos);
        if (eventoActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventoActualizado);
    }


    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable String id) {
        eventosDao.eliminarEvento(id);
        return ResponseEntity.ok().build();
    }

}
