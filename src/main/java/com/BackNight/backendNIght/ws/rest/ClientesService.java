package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.ClientesDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class ClientesService {

    @Autowired
    private ClientesDao clientesDao;

    @PostMapping("/registrar-cliente")
    public ResponseEntity<Clientes> registrarCliente(@RequestBody Clientes cliente) {
        Clientes nuevoCliente = clientesDao.registrarCliente(cliente);
        return ResponseEntity.ok(nuevoCliente);
    }

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody Clientes cliente) {
        System.out.println("Usuario recibido: " + cliente.getUsuarioCliente());
        System.out.println("Contraseña recibida: " + cliente.getContrasenaCliente());

        Clientes clienteEncontrado = clientesDao.loginCliente(cliente.getUsuarioCliente(), cliente.getContrasenaCliente());
        if (clienteEncontrado == null) {
            System.out.println("No se encontró cliente con esas credenciales");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
        return ResponseEntity.ok(clienteEncontrado);
    }

}
