package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventosDao {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AdministradoresRepository administradoresRepository;

    @Transactional(readOnly = true)
    public Evento consultarEventoIndividual(Integer id) {
        // Usa el método del repositorio que carga la discoteca con JOIN FETCH
        // No necesitas limpiar aquí, el DTO se encargará de lo que se envía.
        return eventoRepository.findByIdWithDiscoteca(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        // Usa el método del repositorio con JOIN FETCH para cargar la discoteca
        // ¡CRÍTICO: Usa el nuevo método con JOIN FETCH!
        return eventoRepository.findByAdministradorIdAdminWithDiscoteca(idAdmin);
    }

    @Transactional(readOnly = true)
    public List<Evento> obtenerTodosEventos() {
        // Usa el nuevo método del repositorio que carga la discoteca con JOIN FETCH
        return eventoRepository.findAllWithDiscoteca();
    }

    @Transactional(readOnly = true)
    public List<Evento> consultarEventosPorDiscotecaNit(Integer nitDiscoteca) {
        // Usa el nuevo método del repositorio con JOIN FETCH para cargar la discoteca
        // ¡CRÍTICO: Usa el nuevo método con JOIN FETCH!
        return eventoRepository.findByDiscoteca_NitWithDiscoteca(nitDiscoteca);
    }

    @Transactional
    public Evento registrarEvento(Evento evento) {
        if (evento.getAdministrador() == null || evento.getAdministrador().getIdAdmin() == null) {
            throw new IllegalArgumentException("El objeto Evento debe tener un Administrador asociado con un ID válido.");
        }
        // No necesitas debugs de imagen aquí, eso es más del servicio o de un logger.
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento actualizarEvento(Evento evento, Integer idAdmin) {
        Optional<Evento> existingEventoOpt = eventoRepository.findById(evento.getIdEvento());
        if (existingEventoOpt.isPresent()) {
            Evento existingEvento = existingEventoOpt.get();
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                // Mantener imagen si no se proporciona nueva
                if (evento.getImagen() == null && existingEvento.getImagen() != null) {
                    evento.setImagen(existingEvento.getImagen());
                }
                // Asegurar que el administrador y la discoteca se mantengan si no se modifican
                evento.setAdministrador(existingEvento.getAdministrador());
                if (evento.getDiscoteca() == null && existingEvento.getDiscoteca() != null) {
                    evento.setDiscoteca(existingEvento.getDiscoteca());
                }
                return eventoRepository.save(evento);
            }
        }
        return null;
    }

    @Transactional
    public boolean eliminarEvento(Integer id, Integer idAdmin) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            if (evento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                eventoRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
}