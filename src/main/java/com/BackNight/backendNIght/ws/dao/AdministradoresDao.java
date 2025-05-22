package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradoresDao {

    @Autowired
    private AdministradoresRepository administradoresRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Administradores registrarAdministrador(Administradores administrador) {
        administrador.setContrasenaAdmin(passwordEncoder.encode(administrador.getContrasenaAdmin()));
        return administradoresRepository.save(administrador);
    }

    public Administradores loginAdministradores(String usuarioAdmin, String contrasenaAdmin) {
        Administradores admin = administradoresRepository.findByUsuarioAdmin(usuarioAdmin);
        if (admin != null && passwordEncoder.matches(contrasenaAdmin, admin.getContrasenaAdmin())) {
            return admin;
        }
        return null;
    }

    public List<Administradores> obtenerTodos() {
        return administradoresRepository.findAll();
    }
}
