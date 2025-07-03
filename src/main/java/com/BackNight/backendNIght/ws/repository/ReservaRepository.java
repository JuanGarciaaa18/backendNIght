package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // Importar Optional

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    // Si en el futuro necesitas filtrar por cliente:
    List<Reserva> findByCliente_IdCliente(Integer idCliente);

    // --- NUEVO MÉTODO ---
    Optional<Reserva> findByPreferenceId(String preferenceId);
}