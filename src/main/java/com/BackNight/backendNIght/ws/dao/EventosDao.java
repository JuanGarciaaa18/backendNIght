package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Importante!

import java.util.List;
import java.util.Optional;

@Service
public class EventosDao {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AdministradoresRepository administradoresRepository;

    @Transactional(readOnly = true) // Asegura que la operación esté dentro de una transacción
    public Evento consultarEventoIndividual(Integer id) {
        // Usar el nuevo método que carga la discoteca con JOIN FETCH
        Optional<Evento> eventoOptional = eventoRepository.findByIdWithDiscoteca(id);
        Evento evento = eventoOptional.orElse(null);

        if (evento != null) {
            System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID: " + evento.getIdEvento() + ", Imagen Longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
            if (evento.getDiscoteca() != null) {
                // Ahora, getDiscoteca() ya debería estar completamente inicializado
                Integer discotecaNit = evento.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventoIndividual - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON si no son necesarias en el frontend
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
                evento.getDiscoteca().setEventos(null); // ¡Añadir esta línea para evitar ciclos con la lista de eventos de la discoteca!
            } else {
                System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID " + evento.getIdEvento() + " tiene referencia a discoteca NULA (¡Esto no debería pasar con JOIN FETCH si hay datos!).");
            }
        }
        return evento;
    }

    @Transactional(readOnly = true)
    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        // Usar el nuevo método que carga la discoteca con JOIN FETCH
        List<Evento> eventos = eventoRepository.findByAdministrador_IdAdmin(idAdmin);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // ¡Añadir esta línea!
            } else {
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (¡Esto no debería pasar con JOIN FETCH si hay datos!).");
            }
        }
        return eventos;
    }

    @Transactional(readOnly = true)
    public List<Evento> obtenerTodosEventos() {
        // Usar el nuevo método que carga la discoteca con JOIN FETCH
        List<Evento> eventos = eventoRepository.findAllWithDiscoteca();
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerTodosEventos - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // ¡Añadir esta línea!
            } else {
                System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (¡Esto no debería pasar con JOIN FETCH si hay datos!).");
            }
        }
        return eventos;
    }

    @Transactional(readOnly = true)
    public List<Evento> consultarEventosPorDiscotecaNit(Integer nitDiscoteca) {
        // Usar el nuevo método que carga la discoteca con JOIN FETCH
        List<Evento> eventos = eventoRepository.findByDiscoteca_Nit(nitDiscoteca);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // ¡Añadir esta línea!
            } else {
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (¡Esto no debería pasar con JOIN FETCH si hay datos!).");
            }
        }
        return eventos;
    }

    @Transactional // Las operaciones de escritura deben ser transaccionales
    public Evento registrarEvento(Evento evento) {
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

    @Transactional
    public Evento actualizarEvento(Evento evento, Integer idAdmin) {
        Optional<Evento> existingEventoOpt = eventoRepository.findById(evento.getIdEvento());
        if (existingEventoOpt.isPresent()) {
            Evento existingEvento = existingEventoOpt.get();
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                if (evento.getImagen() == null && existingEvento.getImagen() != null) {
                    evento.setImagen(existingEvento.getImagen());
                }
                // Asegurarse de que el administrador se mantenga si no se proporciona uno nuevo
                evento.setAdministrador(existingEvento.getAdministrador());

                // Asegurarse de que la discoteca se mantenga si no se proporciona una nueva
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

    @Transactional
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