package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento e LEFT JOIN FETCH r.cliente c WHERE c.idCliente = :idCliente")
    List<Reserva> findByClienteIdClienteWithEvento(@Param("idCliente") Integer idCliente);

    List<Reserva> findByCliente_IdCliente(Integer idCliente);
    Optional<Reserva> findByPreferenceId(String preferenceId);

    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente")
    List<Reserva> findAllWithEventoAndCliente();

    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente WHERE r.idReserva = :id")
    Optional<Reserva> findByIdWithEventoAndCliente(@Param("id") Integer id);
}
