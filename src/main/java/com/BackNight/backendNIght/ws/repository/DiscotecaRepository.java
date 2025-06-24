package com.BackNight.backendNIght.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.BackNight.backendNIght.ws.entity.Discoteca;

import java.util.List;
import java.util.Optional;

public interface DiscotecaRepository extends JpaRepository<Discoteca, Integer> {
    // Nuevo método para encontrar discotecas por el ID del administrador
    List<Discoteca> findByAdministrador_IdAdmin(Integer idAdmin);

    // Puedes añadir este si lo necesitas para buscar por NIT de forma explícita
    Optional<Discoteca> findByNit(Integer nit);
}
