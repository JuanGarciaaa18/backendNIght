package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> { // Cambiado a Integer
    // Nuevo método para encontrar eventos por el ID del administrador
    List<Evento> findByAdministrador_IdAdmin(Integer idAdmin);
}
