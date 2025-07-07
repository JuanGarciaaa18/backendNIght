package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    // MÉTODO CORREGIDO: Para encontrar reservas por ID de cliente, cargando el evento Y EL CLIENTE
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente c WHERE c.idCliente = :idCliente")
    List<Reserva> findByClienteIdClienteWithEvento(@Param("idCliente") Integer idCliente);

    // Métodos que ya tenías:
    List<Reserva> findByCliente_IdCliente(Integer idCliente); // Podrías eliminar este si solo usas el de arriba
    Optional<Reserva> findByPreferenceId(String preferenceId);

    // Método para cargar todas las reservas con sus eventos y clientes para los DTOs
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente")
    List<Reserva> findAllWithEventoAndCliente();

    // Método para cargar una reserva individual con su evento y cliente para el DTO
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente WHERE r.idReserva = :id")
    Optional<Reserva> findByIdWithEventoAndCliente(@Param("id") Integer id);
}
