package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.AdministradoresDao;
import com.BackNight.backendNIght.ws.entity.Administradores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class AdministradoresService {

    @Autowired
    private AdministradoresDao administradoresDao;

    @PostMapping("/registrarAdmin")
    public Administradores registrarAdministrador(@RequestBody Administradores administrador) {
        return administradoresDao.registrarAdministrador(administrador);
    }

    @PostMapping("/loginAdmin")
    public Administradores loginAdministradores(@RequestBody Administradores admin) {
        return administradoresDao.loginAdministradores(admin.getUsuarioAdmin(), admin.getContrasenaAdmin());
    }
}
