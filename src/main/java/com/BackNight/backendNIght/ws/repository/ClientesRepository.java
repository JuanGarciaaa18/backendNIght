package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Integer> {
    Clientes findByUsuarioCliente(String usuarioCliente);
    Clientes findByCorreo(String correo); // NUEVO
}