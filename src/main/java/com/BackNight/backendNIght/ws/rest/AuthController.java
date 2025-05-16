package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.dao.ClientesDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private ClientesDao clientesDao;

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody Clientes cliente) {
        Clientes clienteEncontrado = clientesDao.loginCliente(cliente.getUsuarioCliente(), cliente.getContrasenaCliente());
        if (clienteEncontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = JwtUtil.generateToken(clienteEncontrado.getUsuarioCliente());

        return ResponseEntity.ok(new LoginResponse(clienteEncontrado.getUsuarioCliente(), clienteEncontrado.getCorreo(), token));
    }

    public static class LoginResponse {
        private String usuario;
        private String correo;
        private String token;

        public LoginResponse(String usuario, String correo, String token) {
            this.usuario = usuario;
            this.correo = correo;
            this.token = token;
        }

        public String getUsuario() { return usuario; }
        public String getCorreo() { return correo; }
        public String getToken() { return token; }
    }
}