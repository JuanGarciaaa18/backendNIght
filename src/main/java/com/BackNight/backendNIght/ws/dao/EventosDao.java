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
        return eventoRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene una lista de eventos filtrados por el ID del administrador.
     * Se limpian las referencias a entidades relacionadas para evitar problemas de serialización JSON.
     * @param idAdmin El ID del administrador para filtrar.
     * @return Una lista de eventos que pertenecen al administrador.
     */
    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        List<Evento> eventos = eventoRepository.findByAdministrador_IdAdmin(idAdmin);
        // Limpiar referencias para evitar ciclos JSON si es necesario
        for (Evento e : eventos) {
            e.setAdministrador(null); // No enviar el administrador completo para evitar recursión
            if (e.getDiscoteca() != null) {
                e.getDiscoteca().setAdministrador(null); // También limpiar la referencia al admin en la discoteca del evento
                e.getDiscoteca().setZonas(null); // Y zonas si es necesario
            }
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
        // Limpiar referencias para evitar ciclos JSON si es necesario
        for (Evento e : eventos) {
            e.setAdministrador(null); // No enviar el administrador completo en la lista pública
            if (e.getDiscoteca() != null) {
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
            }
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
        evento.setAdministrador(admin); // Asigna el administrador
        return eventoRepository.save(evento);
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
            // Asegurarse de que el administrador del evento coincida con el que intenta actualizar
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                evento.setAdministrador(existingEvento.getAdministrador()); // Mantener el mismo admin

                // Si la discoteca no se envía en el body de actualización, mantener la existente
                if (evento.getDiscoteca() == null && existingEvento.getDiscoteca() != null) {
                    evento.setDiscoteca(existingEvento.getDiscoteca());
                }
                return eventoRepository.save(evento);
            }
        }
        return null; // No encontrado o no autorizado
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
                eventoRepository.deleteById(id);
                return true;
            }
        }
        return false; // No encontrado o no autorizado
    }
}
