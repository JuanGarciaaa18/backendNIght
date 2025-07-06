package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- ¡Este import es crucial!
import org.springframework.data.repository.query.Param; // <-- ¡Este import es crucial!
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE e.administrador.idAdmin = :idAdmin")
    List<Evento> findByAdministrador_IdAdmin(@Param("idAdmin") Integer idAdmin);

    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE d.nit = :nitDiscoteca")
    List<Evento> findByDiscoteca_Nit(@Param("nitDiscoteca") Integer nitDiscoteca);

    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d WHERE e.idEvento = :id")
    Optional<Evento> findByIdWithDiscoteca(@Param("id") Integer id);

    @Query("SELECT e FROM Evento e JOIN FETCH e.discoteca d")
    List<Evento> findAllWithDiscoteca();
}