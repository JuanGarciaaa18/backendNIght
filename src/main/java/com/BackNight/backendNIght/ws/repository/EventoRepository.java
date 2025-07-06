package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional; // Importar Optional

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    // 1. Para consultar un evento individual con su discoteca
    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.discoteca d WHERE e.idEvento = :idEvento")
    Optional<Evento> findByIdWithDiscoteca(@Param("idEvento") Integer idEvento);

    // 2. Para obtener eventos por administrador con su discoteca (¡CRÍTICO para tu caso!)
    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.discoteca d WHERE e.administrador.idAdmin = :adminId")
    List<Evento> findByAdministradorIdAdminWithDiscoteca(@Param("adminId") Integer adminId);

    // 3. Para obtener todos los eventos con su discoteca (para /eventos-list)
    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.discoteca")
    List<Evento> findAllWithDiscoteca();

    // 4. Para obtener eventos por NIT de discoteca con su discoteca (¡CRÍTICO para tu caso!)
    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.discoteca d WHERE d.nit = :nitDiscoteca")
    List<Evento> findByDiscoteca_NitWithDiscoteca(@Param("nitDiscoteca") Integer nitDiscoteca);

    // Nota: findByAdministrador_IdAdmin y findByDiscoteca_Nit generados por Spring Data JPA
    // NO usan JOIN FETCH automáticamente. Por eso necesitas las @Query personalizadas.
}