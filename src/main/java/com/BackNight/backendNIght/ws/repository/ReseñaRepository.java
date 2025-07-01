package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Reseña;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReseñaRepository extends JpaRepository<Reseña, Integer> {

    // Buscar reseñas por el NIT de la discoteca
    List<Reseña> findByDiscoteca_Nit(Integer nitDiscoteca);

    // Buscar reseñas por el ID del cliente
    List<Reseña> findByCliente_IdCliente(Integer idCliente);

    // Opcional: Buscar una reseña específica de un cliente para una discoteca
    Reseña findByCliente_IdClienteAndDiscoteca_Nit(Integer idCliente, Integer nitDiscoteca);
}