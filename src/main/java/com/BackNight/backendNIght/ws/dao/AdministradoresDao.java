package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministradoresDao {

    @Autowired
    private AdministradoresRepository administradoresRepository;

    public Administradores registrarAdministrador(Administradores administrador) {
        return administradoresRepository.save(administrador);
    }

    public Administradores loginAdministradores(String usuarioAdmin, String contrasenaAdmin) {
        return administradoresRepository.findByUsuarioAdminAndContrasenaAdmin(usuarioAdmin, contrasenaAdmin);
    }

}
