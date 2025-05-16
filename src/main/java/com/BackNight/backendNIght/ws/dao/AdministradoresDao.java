package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class AdministradoresDao {

    @Autowired
    private static AdministradoresRepository AdministradoresRepository;

    public static Administradores registrarAdministrador(Administradores administrador) {
        return AdministradoresRepository.save(administrador);
    }

    public static Administradores loginAdministradores(String usuarioAdministrador, String contrasenaAdministrador) {
        return AdministradoresRepository.findByUsuarioAministradorAndContrasenaAdministrador(usuarioAdministrador, contrasenaAdministrador);
    }
}
