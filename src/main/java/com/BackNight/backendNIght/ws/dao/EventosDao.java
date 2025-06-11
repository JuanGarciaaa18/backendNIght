package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventosDao {

    @Autowired
    private EventoRepository eventoRepository;

    public Evento consultarEventoIndividual(Integer id) {
        return eventoRepository.findById(id).orElse(null);
    }

    public List<Evento> obtenerListaEventos() {
        return eventoRepository.findAll();
    }

    public Evento registrarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Evento evento) {
        if (eventoRepository.existsById(evento.getIdEvento())) {
            return eventoRepository.save(evento);
        }
        return null;
    }

    public void eliminarEvento(Integer id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
        }
    }
}
