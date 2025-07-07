package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository; // Asegúrate de tener esta importación

import java.util.List;
import java.util.Optional;

@Repository // Asegúrate de que tenga esta anotación
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    // Esta consulta es para "Mis Reservas" del cliente, y ya hace FETCH de cliente y evento
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento e LEFT JOIN FETCH r.cliente c WHERE c.idCliente = :idCliente")
    List<Reserva> findByClienteIdClienteWithEvento(@Param("idCliente") Integer idCliente);

    List<Reserva> findByCliente_IdCliente(Integer idCliente);
    Optional<Reserva> findByPreferenceId(String preferenceId);

    // --- ¡ESTA ES LA CONSULTA ORIGINAL PARA EL PANEL DE ADMINISTRADOR (SIN FILTRO)! ---
    // La mantenemos por si la necesitas para un super-admin o depuración,
    // pero el endpoint /admin/reservas ahora usará la nueva consulta filtrada.
    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente")
    List<Reserva> findAllWithEventoAndCliente();

    @Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.evento LEFT JOIN FETCH r.cliente WHERE r.idReserva = :id")
    Optional<Reserva> findByIdWithEventoAndCliente(@Param("id") Integer id);

    // --- NUEVO MÉTODO CRÍTICO: Obtener reservas por IDs de Discoteca (NITs) ---
    // Esto es para que un administrador vea solo las reservas de SUS discotecas.
    // Usamos 'e.discoteca.nit' porque 'nit' es el ID de la entidad Discoteca.
    @Query("SELECT r FROM Reserva r JOIN FETCH r.evento e JOIN FETCH e.discoteca d LEFT JOIN FETCH r.cliente c WHERE d.nit IN :discotecaNits")
    List<Reserva> findByEventoDiscotecaNitIn(@Param("discotecaNits") List<Integer> discotecaNits);
}