package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // Método para encontrar eventos por el ID del administrador
    List<Evento> findByAdministrador_IdAdmin(Integer idAdmin);

    // Método para encontrar eventos por el NIT de la discoteca
    // Asegúrate de que el campo en la entidad Discoteca se llama 'nit'
    List<Evento> findByDiscoteca_Nit(Integer nitDiscoteca);
}