package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class AdministradoresDao {

    @Autowired
    private AdministradoresRepository AdministradoresRepository;

    public Administradores registrarAdministrador(Administradores administrador) {
        return AdministradoresRepository.save(Administradores);
    }

    public Administradores loginAdministradores(String usuarioAdministrador, String contrasenaAdministrador) {
        return AdministradoresRepository.findByUsuarioAdministradorAndContrasenaAdministrador(usuarioAdministraor, contrasenaAdministrador);
    }
}
