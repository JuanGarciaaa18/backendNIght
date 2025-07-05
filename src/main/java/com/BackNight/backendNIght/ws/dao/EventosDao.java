package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
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
    private AdministradoresRepository administradoresRepository;

    /**
     * Consulta un evento individual por su ID.
     * @param id El ID del evento.
     * @return El evento encontrado o null si no existe.
     */
    public Evento consultarEventoIndividual(Integer id) {
        Evento evento = eventoRepository.findById(id).orElse(null);
        if (evento != null) {
            System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID: " + evento.getIdEvento() + ", Imagen Longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
            // --- INICIO CAMBIO AQUÍ ---
            if (evento.getDiscoteca() != null) {
                // Acceder al NIT para forzar la carga de la discoteca
                Integer discotecaNit = evento.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventoIndividual - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos (ya lo haces en el service, pero no está de más aquí)
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID " + evento.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
            // --- FIN CAMBIO AQUÍ ---
        }
        return evento;
    }

    /**
     * Obtiene una lista de eventos filtrados por el ID del administrador.
     * Se limpian las referencias a entidades relacionadas para evitar problemas de serialización JSON.
     * @param idAdmin El ID del administrador para filtrar.
     * @return Una lista de eventos que pertenecen al administrador.
     */
    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        List<Evento> eventos = eventoRepository.findByAdministrador_IdAdmin(idAdmin);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            // --- INICIO CAMBIO AQUÍ ---
            if (e.getDiscoteca() != null) {
                // Acceder al NIT para forzar la carga de la discoteca
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
            // --- FIN CAMBIO AQUÍ ---
        }
        return eventos;
    }

    /**
     * Obtiene una lista de *todos* los eventos disponibles.
     * Limpia referencias para evitar problemas de serialización JSON.
     * @return Una lista de todos los eventos.
     */
    public List<Evento> obtenerTodosEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            // --- INICIO CAMBIO AQUÍ ---
            if (e.getDiscoteca() != null) {
                // Acceder al NIT para forzar la carga de la discoteca
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerTodosEventos - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
            // --- FIN CAMBIO AQUÍ ---
        }
        return eventos;
    }

    /**
     * Consulta una lista de eventos por el NIT de la discoteca a la que pertenecen.
     * @param nitDiscoteca El NIT de la discoteca.
     * @return Una lista de eventos asociados a esa discoteca.
     */
    public List<Evento> consultarEventosPorDiscotecaNit(Integer nitDiscoteca) {
        List<Evento> eventos = eventoRepository.findByDiscoteca_Nit(nitDiscoteca);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null);
            // --- INICIO CAMBIO AQUÍ ---
            if (e.getDiscoteca() != null) {
                // Acceder al NIT para forzar la carga de la discoteca
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - NIT de Discoteca accedido: " + discotecaNit);
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            } else {
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA.");
            }
            // --- FIN CAMBIO AQUÍ ---
        }
        return eventos;
    }

    /**
     * Registra un nuevo evento, asociándolo con un administrador.
     * @param evento El evento a registrar.
     * @param idAdmin El ID del administrador que registra el evento.
     * @return El evento registrado.
     * @throws RuntimeException Si el administrador no es encontrado.
     */
    public Evento registrarEvento(Evento evento, Integer idAdmin) {
        Administradores admin = administradoresRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + idAdmin));
        evento.setAdministrador(admin);

        // --- INICIO CAMBIO OPCIONAL AQUÍ (para debugging al guardar) ---
        if (evento.getDiscoteca() != null) {
            System.out.println("DEBUG DAO: registrarEvento - Recibido NIT Discoteca: " + evento.getDiscoteca().getNit());
        } else {
            System.out.println("DEBUG DAO: registrarEvento - Discoteca en evento es NULA al registrar.");
        }
        // --- FIN CAMBIO OPCIONAL AQUÍ ---

        System.out.println("DEBUG DAO: registrarEvento - Recibida imagen Base64 para guardar, longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
        Evento savedEvento = eventoRepository.save(evento);
        System.out.println("DEBUG DAO: registrarEvento - Imagen Base64 guardada, longitud: " + (savedEvento.getImagen() != null ? savedEvento.getImagen().length() : "null"));
        return savedEvento;
    }

    /**
     * Actualiza un evento existente, validando la propiedad del administrador.
     * @param evento El evento con los datos actualizados.
     * @param idAdmin El ID del administrador que intenta actualizar.
     * @return El evento actualizado o null si no existe o no pertenece al administrador.
     */
    public Evento actualizarEvento(Evento evento, Integer idAdmin) {
        Optional<Evento> existingEventoOpt = eventoRepository.findById(evento.getIdEvento());
        if (existingEventoOpt.isPresent()) {
            Evento existingEvento = existingEventoOpt.get();
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                // Si la imagen nueva es null, mantén la existente (para no borrarla si no se carga una nueva)
                if (evento.getImagen() == null && existingEvento.getImagen() != null) {
                    evento.setImagen(existingEvento.getImagen());
                }

                // Mantenemos el mismo administrador original para el evento
                evento.setAdministrador(existingEvento.getAdministrador());

                // --- INICIO CAMBIO OPCIONAL AQUÍ (para asegurar que la discoteca se mantiene si no se envía nueva) ---
                // Si el evento recibido NO tiene discoteca, pero el existente SÍ, mantenemos la discoteca existente.
                if (evento.getDiscoteca() == null && existingEvento.getDiscoteca() != null) {
                    evento.setDiscoteca(existingEvento.getDiscoteca());
                    // Forzamos la carga del NIT también aquí si se mantuvo la discoteca existente
                    System.out.println("DEBUG DAO: actualizarEvento - Manteniendo Discoteca existente con NIT: " + existingEvento.getDiscoteca().getNit());
                } else if (evento.getDiscoteca() != null) {
                    // Si se envió una nueva discoteca (con un NIT), la usamos.
                    // Accedemos al NIT de la discoteca recién enviada para asegurarnos de que se maneje.
                    System.out.println("DEBUG DAO: actualizarEvento - Nueva Discoteca recibida con NIT: " + evento.getDiscoteca().getNit());
                }
                // --- FIN CAMBIO OPCIONAL AQUÍ ---

                System.out.println("DEBUG DAO: actualizarEvento - Recibida imagen Base64 para actualizar, longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
                Evento updatedEvento = eventoRepository.save(evento);
                System.out.println("DEBUG DAO: actualizarEvento - Imagen Base64 actualizada, longitud: " + (updatedEvento.getImagen() != null ? updatedEvento.getImagen().length() : "null"));
                return updatedEvento;
            }
        }
        return null;
    }

    /**
     * Elimina un evento, validando la propiedad del administrador.
     * @param id El ID del evento a eliminar.
     * @param idAdmin El ID del administrador que intenta eliminar.
     * @return true si el evento fue eliminado, false en caso contrario.
     */
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