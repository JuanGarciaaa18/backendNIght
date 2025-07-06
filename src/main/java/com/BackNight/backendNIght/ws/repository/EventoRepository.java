package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // Método para encontrar eventos por el ID del administrador
    // ¡IMPORTANTE! Usamos JOIN FETCH para cargar la discoteca también
    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE e.administrador.idAdmin = :idAdmin")
    List<Evento> findByAdministrador_IdAdmin(@Param("idAdmin") Integer idAdmin);

    // Método para encontrar eventos por el NIT de la discoteca
    // ¡IMPORTANTE! Usamos JOIN FETCH para cargar la discoteca también
    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE d.nit = :nitDiscoteca")
    List<Evento> findByDiscoteca_Nit(@Param("nitDiscoteca") Integer nitDiscoteca);

    // Nuevo método para obtener un evento individual con su discoteca cargada
    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE e.idEvento = :id")
    Optional<Evento> findByIdWithDiscoteca(@Param("id") Integer id);

    // Nuevo método para obtener TODOS los eventos con sus discotecas cargadas
    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d")
    List<Evento> findAllWithDiscoteca();
}