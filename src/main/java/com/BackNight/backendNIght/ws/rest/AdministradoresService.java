package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.AdministradoresDao;
import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class AdministradoresService {

    @Autowired
    private AdministradoresDao administradoresDao;

    @PostMapping("/registrar-administrador")
    public ResponseEntity<Administradores> registrarAdministrador(@RequestBody Administradores administrador) {
        Administradores nuevoAdministrador = administradoresDao.registrarAdministrador(administrador);
        return ResponseEntity.ok(nuevoAdministrador);
    }


    @PostMapping("/login-administrador")
    public ResponseEntity<?> loginAdministradores(@RequestBody Administradores administrador) {
        System.out.println("Usuario recibido: " + administrador.getUsuarioAdmin());
        System.out.println("Contraseña recibida: " + administrador.getContrasenaAdmin());

        Administradores administradorEncontrado = administradoresDao.loginAdministradores(administrador.getUsuarioAdmin(), administrador.getContrasenaAdmin());
        if (administradorEncontrado == null) {
            System.out.println("No se encontró administrador con esas credenciales");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
        return ResponseEntity.ok(administradorEncontrado);
    }


}