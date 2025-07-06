package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores; // Se mantiene si otros métodos lo necesitan
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository; // Se mantiene si otros métodos lo necesitan
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventosDao {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AdministradoresRepository administradoresRepository; // Se mantiene si lo usas en otros métodos del DAO

    public Evento consultarEventoIndividual(Integer id) {
        Evento evento = eventoRepository.findById(id).orElse(null);
        if (evento != null) {
            System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID: " + evento.getIdEvento() + ", Imagen Longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
            if (evento.getDiscoteca() != null) {
                Integer discotecaNit = evento.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventoIndividual - NIT de Discoteca accedido: " + discotecaNit);
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID " + evento.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
        }
        return evento;
    }

    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        List<Evento> eventos = eventoRepository.findByAdministrador_IdAdmin(idAdmin);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
        }
        return eventos;
    }

    public List<Evento> obtenerTodosEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerTodosEventos - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
        }
        return eventos;
    }

    public List<Evento> consultarEventosPorDiscotecaNit(Integer nitDiscoteca) {
        List<Evento> eventos = eventoRepository.findByDiscoteca_Nit(nitDiscoteca);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
        }
        return eventos;
    }

    /**
     * Registra un nuevo evento. El objeto Evento debe venir con su Administrador ya seteado.
     * @param evento El evento a registrar.
     * @return El evento registrado.
     */
    public Evento registrarEvento(Evento evento) { // <-- ¡CAMBIO AQUÍ! Ya no recibe idAdmin
        // Verificación adicional, aunque el servicio debería asegurarlo
        if (evento.getAdministrador() == null || evento.getAdministrador().getIdAdmin() == null) {
            throw new IllegalArgumentException("El objeto Evento debe tener un Administrador asociado con un ID válido.");
        }

        if (evento.getDiscoteca() != null) {
            System.out.println("DEBUG DAO: registrarEvento - Recibido NIT Discoteca: " + evento.getDiscoteca().getNit());
        } else {
            System.out.println("DEBUG DAO: registrarEvento - Discoteca en evento es NULA al registrar.");
        }

        System.out.println("DEBUG DAO: registrarEvento - Recibida imagen Base64 para guardar, longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
        Evento savedEvento = eventoRepository.save(evento);
        System.out.println("DEBUG DAO: registrarEvento - Imagen Base64 guardada, longitud: " + (savedEvento.getImagen() != null ? savedEvento.getImagen().length() : "null"));
        return savedEvento;
    }

    public Evento actualizarEvento(Evento evento, Integer idAdmin) {
        Optional<Evento> existingEventoOpt = eventoRepository.findById(evento.getIdEvento());
        if (existingEventoOpt.isPresent()) {
            Evento existingEvento = existingEventoOpt.get();
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                if (evento.getImagen() == null && existingEvento.getImagen() != null) {
                    evento.setImagen(existingEvento.getImagen());
                }
                evento.setAdministrador(existingEvento.getAdministrador());

                if (evento.getDiscoteca() == null && existingEvento.getDiscoteca() != null) {
                    evento.setDiscoteca(existingEvento.getDiscoteca());
                    System.out.println("DEBUG DAO: actualizarEvento - Manteniendo Discoteca existente con NIT: " + existingEvento.getDiscoteca().getNit());
                } else if (evento.getDiscoteca() != null) {
                    System.out.println("DEBUG DAO: actualizarEvento - Nueva Discoteca recibida con NIT: " + evento.getDiscoteca().getNit());
                }

                System.out.println("DEBUG DAO: actualizarEvento - Recibida imagen Base64 para actualizar, longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
                Evento updatedEvento = eventoRepository.save(evento);
                System.out.println("DEBUG DAO: actualizarEvento - Imagen Base64 actualizada, longitud: " + (updatedEvento.getImagen() != null ? updatedEvento.getImagen().length() : "null"));
                return updatedEvento;
            }
        }
        return null;
    }

    public boolean eliminarEvento(Integer id, Integer idAdmin) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            if (evento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                System.out.println("DEBUG DAO: eliminarEvento - Eliminando evento con ID: " + id);
                eventoRepository.deleteById(id);
                return true;
            }
        }
        System.out.println("DEBUG DAO: eliminarEvento - No se pudo eliminar evento con ID: " + id + " (No encontrado o no autorizado)");
        return false;
    }
}