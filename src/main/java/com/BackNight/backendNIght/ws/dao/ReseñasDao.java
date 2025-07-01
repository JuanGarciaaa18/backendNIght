package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.entity.Discoteca;
import com.BackNight.backendNIght.ws.entity.Reseña;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import com.BackNight.backendNIght.ws.repository.DiscotecaRepository; // Necesitarás este repositorio
import com.BackNight.backendNIght.ws.repository.ReseñaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReseñasDao {

    @Autowired
    private ReseñaRepository reseñaRepository;

    @Autowired
    private ClientesRepository clientesRepository; // Asume que tienes un ClienteRepository

    @Autowired
    private DiscotecaRepository discotecaRepository; // Asume que tienes un DiscotecaRepository

    /**
     * Registra una nueva reseña para una discoteca por un cliente.
     * @param reseña La reseña a registrar (debe contener puntuacion y comentario).
     * @param idCliente El ID del cliente que crea la reseña.
     * @param nitDiscoteca El NIT de la discoteca a la que se hace la reseña.
     * @return La reseña guardada.
     * @throws RuntimeException Si el cliente o la discoteca no son encontrados, o si ya existe una reseña del cliente para esa discoteca.
     */
    public Reseña registrarReseña(Reseña reseña, Integer idCliente, Integer nitDiscoteca) {
        Clientes cliente = clientesRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));

        Discoteca discoteca = discotecaRepository.findById(nitDiscoteca)
                .orElseThrow(() -> new RuntimeException("Discoteca no encontrada con NIT: " + nitDiscoteca));

        // Opcional: Verificar si el cliente ya ha dejado una reseña para esta discoteca
        // Reseña existingReseña = reseñaRepository.findByCliente_IdClienteAndDiscoteca_Nit(idCliente, nitDiscoteca);
        // if (existingReseña != null) {
        //     throw new RuntimeException("Ya existe una reseña de este cliente para esta discoteca.");
        // }

        reseña.setCliente(cliente);
        reseña.setDiscoteca(discoteca);
        reseña.setFechaReseña(LocalDateTime.now()); // Establece la fecha y hora actual

        System.out.println("DEBUG DAO: registrarReseña - Guardando reseña del cliente " + idCliente + " para discoteca " + nitDiscoteca);
        return reseñaRepository.save(reseña);
    }

    /**
     * Obtiene todas las reseñas de una discoteca específica.
     * @param nitDiscoteca El NIT de la discoteca.
     * @return Lista de reseñas para la discoteca.
     */
    public List<Reseña> obtenerReseñasPorDiscoteca(Integer nitDiscoteca) {
        List<Reseña> reseñas = reseñaRepository.findByDiscoteca_Nit(nitDiscoteca);
        // Limpiar referencias para evitar ciclos o datos innecesarios en la respuesta JSON
        for (Reseña r : reseñas) {
            if (r.getCliente() != null) {
                // Solo mantener el nombre del cliente para la reseña, por ejemplo
                // r.getCliente().setContrasenaCliente(null);
                // r.getCliente().setUsuarioCliente(null);
                // Si usas @JsonIgnoreProperties en la entidad, esto no es estrictamente necesario aquí
            }
            if (r.getDiscoteca() != null) {
                r.setDiscoteca(null); // No necesitas la discoteca completa en cada reseña de la lista
            }
        }
        System.out.println("DEBUG DAO: obtenerReseñasPorDiscoteca - Cantidad de reseñas para NIT " + nitDiscoteca + ": " + reseñas.size());
        return reseñas;
    }

    /**
     * Obtiene todas las reseñas realizadas por un cliente específico.
     * @param idCliente El ID del cliente.
     * @return Lista de reseñas del cliente.
     */
    public List<Reseña> obtenerReseñasPorCliente(Integer idCliente) {
        List<Reseña> reseñas = reseñaRepository.findByCliente_IdCliente(idCliente);
        // Limpiar referencias
        for (Reseña r : reseñas) {
            if (r.getCliente() != null) {
                r.setCliente(null); // No necesitas el cliente completo en cada reseña de la lista
            }
            if (r.getDiscoteca() != null) {
                // r.getDiscoteca().setAdministrador(null); // Limpia discoteca si se expone
                // r.getDiscoteca().setZonas(null);
            }
        }
        System.out.println("DEBUG DAO: obtenerReseñasPorCliente - Cantidad de reseñas para cliente " + idCliente + ": " + reseñas.size());
        return reseñas;
    }

    /**
     * Actualiza una reseña existente.
     * @param reseña La reseña con los datos actualizados.
     * @param idCliente El ID del cliente que intenta actualizar (para validación).
     * @return La reseña actualizada o null si no se encuentra o no pertenece al cliente.
     */
    public Reseña actualizarReseña(Reseña reseña, Integer idCliente) {
        Optional<Reseña> existingReseñaOpt = reseñaRepository.findById(reseña.getIdReseña());
        if (existingReseñaOpt.isPresent()) {
            Reseña existingReseña = existingReseñaOpt.get();
            // Asegurarse de que el cliente que actualiza es el dueño de la reseña
            if (existingReseña.getCliente().getIdCliente().equals(idCliente)) {
                existingReseña.setPuntuacion(reseña.getPuntuacion());
                existingReseña.setComentario(reseña.getComentario());
                // No actualizamos la fecha de creación, solo el contenido
                System.out.println("DEBUG DAO: actualizarReseña - Actualizando reseña con ID: " + reseña.getIdReseña());
                return reseñaRepository.save(existingReseña);
            }
        }
        System.out.println("DEBUG DAO: actualizarReseña - Reseña no encontrada o no autorizada para actualizar.");
        return null;
    }

    /**
     * Elimina una reseña.
     * @param idReseña El ID de la reseña a eliminar.
     * @param idCliente El ID del cliente que intenta eliminar (para validación).
     * @return true si la reseña fue eliminada, false en caso contrario.
     */
    public boolean eliminarReseña(Integer idReseña, Integer idCliente) {
        Optional<Reseña> reseñaOpt = reseñaRepository.findById(idReseña);
        if (reseñaOpt.isPresent()) {
            Reseña reseña = reseñaOpt.get();
            // Asegurarse de que el cliente que elimina es el dueño de la reseña
            if (reseña.getCliente().getIdCliente().equals(idCliente)) {
                System.out.println("DEBUG DAO: eliminarReseña - Eliminando reseña con ID: " + idReseña);
                reseñaRepository.deleteById(idReseña);
                return true;
            }
        }
        System.out.println("DEBUG DAO: eliminarReseña - Reseña no encontrada o no autorizada para eliminar.");
        return false;
    }
}