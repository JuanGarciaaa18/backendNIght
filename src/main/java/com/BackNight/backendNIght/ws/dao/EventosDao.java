package com.BackNight.backendNIght.ws.dao;


import com.BackNight.backendNIght.ws.entity.Eventos;

import com.BackNight.backendNIght.ws.repository.EventosRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventosDao {

    @Autowired
    private EventosRepository eventoRepository; // sin static

    public Eventos consultarEventoIndividual(String id_event) {
        return eventoRepository.findById(id_event).orElse(null);
    }

    public List<Eventos> obtenerListaEventos() {
        return eventoRepository.findAll();
    }

    public Eventos registrarEvento(Eventos evento) {
        return eventoRepository.save(evento);
    }

    public Eventos actualizarEvento(Eventos evento) {
        if (eventoRepository.existsById(evento.getId_event())) {
            return eventoRepository.save(evento);
        }
        return null;
    }

    public void eliminarEvento(String id_event) {
        if (eventoRepository.existsById(id_event)) {
            eventoRepository.deleteById(id_event);
        }
    }
}
