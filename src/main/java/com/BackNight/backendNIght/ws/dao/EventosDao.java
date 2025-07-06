package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores; // Asegúrate de que este import sea correcto
import com.BackNight.backendNIght.ws.entity.Evento;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository; // Asegúrate de que este import sea correcto
import com.BackNight.backendNIght.ws.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para el manejo de transacciones

import java.util.List;
import java.util.Optional;

@Service
public class EventosDao {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AdministradoresRepository administradoresRepository; // Necesario si manejas Administradores aquí

    /**
     * Consulta un evento individual por su ID, asegurando que la discoteca asociada se cargue completamente.
     * @param id El ID del evento a consultar.
     * @return El objeto Evento si se encuentra, o null si no.
     */
    @Transactional(readOnly = true) // Importante para las operaciones de lectura
    public Evento consultarEventoIndividual(Integer id) {
        // Usa el método del repositorio que carga la discoteca con JOIN FETCH
        Optional<Evento> eventoOptional = eventoRepository.findByIdWithDiscoteca(id);
        Evento evento = eventoOptional.orElse(null);

        if (evento != null) {
            // Logs de depuración (puedes eliminarlos en producción)
            System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID: " + evento.getIdEvento() + ", Imagen Longitud: " + (evento.getImagen() != null ? evento.getImagen().length() : "null"));
            if (evento.getDiscoteca() != null) {
                // Si la discoteca fue cargada con JOIN FETCH, ya debería estar inicializada
                Integer discotecaNit = evento.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventoIndividual - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos de serialización JSON en el controlador
                evento.getDiscoteca().setAdministrador(null);
                evento.getDiscoteca().setZonas(null);
                evento.getDiscoteca().setEventos(null); // CRÍTICO: Limpiar la lista de eventos de la discoteca para evitar recursión
            } else {
                System.out.println("DEBUG DAO: consultarEventoIndividual - Evento ID " + evento.getIdEvento() + " tiene referencia a discoteca NULA (Esto no debería pasar con JOIN FETCH si el dato existe en BD).");
            }
        }
        return evento;
    }

    /**
     * Obtiene una lista de eventos asociados a un administrador específico, cargando sus discotecas.
     * @param idAdmin El ID del administrador.
     * @return Una lista de eventos.
     */
    @Transactional(readOnly = true)
    public List<Evento> obtenerListaEventosPorAdmin(Integer idAdmin) {
        // Usa el método del repositorio con JOIN FETCH para cargar la discoteca
        List<Evento> eventos = eventoRepository.findByAdministrador_IdAdmin(idAdmin);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null); // Limpiar referencia al administrador
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // CRÍTICO: Limpiar la lista de eventos de la discoteca
            } else {
                System.out.println("DEBUG DAO: obtenerListaEventosPorAdmin - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (Esto no debería pasar con JOIN FETCH si el dato existe en BD).");
            }
        }
        return eventos;
    }

    /**
     * Obtiene la lista de todos los eventos, cargando sus discotecas.
     * @return Una lista de todos los eventos.
     */
    @Transactional(readOnly = true)
    public List<Evento> obtenerTodosEventos() {
        // Usa el nuevo método del repositorio que carga la discoteca con JOIN FETCH
        List<Evento> eventos = eventoRepository.findAllWithDiscoteca();
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null); // Limpiar referencia al administrador
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: obtenerTodosEventos - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // CRÍTICO: Limpiar la lista de eventos de la discoteca
            } else {
                System.out.println("DEBUG DAO: obtenerTodosEventos - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (Esto no debería pasar con JOIN FETCH si el dato existe en BD).");
            }
        }
        return eventos;
    }

    /**
     * Consulta eventos por el NIT de una discoteca, cargando las discotecas asociadas.
     * @param nitDiscoteca El NIT de la discoteca.
     * @return Una lista de eventos asociados a esa discoteca.
     */
    @Transactional(readOnly = true)
    public List<Evento> consultarEventosPorDiscotecaNit(Integer nitDiscoteca) {
        // Usa el nuevo método del repositorio que carga la discoteca con JOIN FETCH
        List<Evento> eventos = eventoRepository.findByDiscoteca_Nit(nitDiscoteca);
        for (Evento e : eventos) {
            System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID: " + e.getIdEvento() + ", Imagen Longitud: " + (e.getImagen() != null ? e.getImagen().length() : "null"));
            e.setAdministrador(null); // Limpiar referencia al administrador
            if (e.getDiscoteca() != null) {
                Integer discotecaNit = e.getDiscoteca().getNit();
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - NIT de Discoteca accedido: " + discotecaNit);
                // Limpiar referencias para evitar ciclos JSON
                e.getDiscoteca().setAdministrador(null);
                e.getDiscoteca().setZonas(null);
                e.getDiscoteca().setEventos(null); // CRÍTICO: Limpiar la lista de eventos de la discoteca
            } else {
                System.out.println("DEBUG DAO: consultarEventosPorDiscotecaNit - Evento ID " + e.getIdEvento() + " tiene referencia a discoteca NULA (Esto no debería pasar con JOIN FETCH si el dato existe en BD).");
            }
        }
        return eventos;
    }

    /**
     * Registra un nuevo evento. El objeto Evento debe venir con su Administrador ya seteado.
     * @param evento El evento a registrar.
     * @return El evento registrado.
     */
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

    /**
     * Actualiza un evento existente.
     * @param evento El objeto Evento con los datos actualizados. Debe contener el ID del evento.
     * @param idAdmin El ID del administrador que intenta actualizar el evento (para validación de autorización).
     * @return El evento actualizado, o null si no se encuentra o el administrador no está autorizado.
     */
    @Transactional
    public Evento actualizarEvento(Evento evento, Integer idAdmin) {
        Optional<Evento> existingEventoOpt = eventoRepository.findById(evento.getIdEvento());
        if (existingEventoOpt.isPresent()) {
            Evento existingEvento = existingEventoOpt.get();
            // Verifica que el administrador que intenta actualizar es el mismo que creó el evento
            if (existingEvento.getAdministrador().getIdAdmin().equals(idAdmin)) {
                // Si la imagen nueva es null pero ya existía una, se mantiene la existente
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
        System.out.println("DEBUG DAO: actualizarEvento - No se pudo actualizar evento con ID: " + evento.getIdEvento() + " (No encontrado o no autorizado)");
        return null;
    }

    /**
     * Elimina un evento por su ID, con validación de administrador.
     * @param id El ID del evento a eliminar.
     * @param idAdmin El ID del administrador que intenta eliminar el evento.
     * @return true si se eliminó, false en caso contrario.
     */
    @Transactional
    public boolean eliminarEvento(Integer id, Integer idAdmin) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            // Verifica que el administrador que intenta eliminar es el mismo que creó el evento
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