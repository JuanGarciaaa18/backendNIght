package com.BackNight.backendNIght.ws.repository;

import com.BackNight.backendNIght.ws.entity.Administradores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradoresRepository extends JpaRepository<Administradores, Integer> {
    Administradores findByUsuarioAdmin(String usuarioAdmin);
    Administradores findByCorreoAdmin(String correoAdmin);

}
