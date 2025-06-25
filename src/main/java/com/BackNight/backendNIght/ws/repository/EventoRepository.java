package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // Método para encontrar eventos por el ID del administrador
    List<Evento> findByAdministrador_IdAdmin(Integer idAdmin);

    // ¡CORRECCIÓN AQUÍ! Cambiado de findByDiscoteca_NitDiscoteca a findByDiscoteca_Nit
    List<Evento> findByDiscoteca_Nit(Integer nitDiscoteca); // <-- ¡CORREGIDO!
}