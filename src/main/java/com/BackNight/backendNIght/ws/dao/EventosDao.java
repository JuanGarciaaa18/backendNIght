package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Eventos;
import com.BackNight.backendNIght.ws.repository.EventosRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventosDao {

    @Autowired
    private EventosRepository EventoRepository;

    public Evento consultarDiscotecaIndividual(String id_event) {
        return EventosRepository.findById(id_event).orElse(null);
    }

    public List<Evento> obtenerListaEventos() {
        return EventosRepository.findAll();
    }

    public Evento registrarEvento(Evento discoteca) {
        return EventoRepository.save(evento);
    }

    public Evento actualizarEvento(Evento evento) {
        if (EventoRepository.existsById(evento.getId_event())) {
            return EventoRepository.save(evento);
        }
        return null;
    }

    public void eliminarEvento(String id_event) {
        if (EventoRepository.existsById(id_event)) {
            EventoRepository.deleteById(id_event);
        }
    }
}