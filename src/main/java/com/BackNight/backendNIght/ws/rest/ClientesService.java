package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.util.JwtUtil;
import com.BackNight.backendNIght.ws.dao.ClientesDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.BackNight.backendNIght.ws.service.EmailService;
import com.BackNight.backendNIght.ws.util.CodigoVerificacionStore;
import com.BackNight.backendNIght.ws.dto.ClienteLoginRequestDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ClientesService {

    @Autowired
    private ClientesDao clientesDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodigoVerificacionStore codigoStore;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    private Integer getClienteIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                // Esta es la línea que extrae el ID del token
                return JwtUtil.extractIdUsuarioFromToken(token);
            } catch (Exception e) {
                System.err.println("Error al extraer ID de cliente del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    // ... (otros métodos como registrarCliente, actualizarCliente, etc.) ...

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginClientes(@Valid @RequestBody ClienteLoginRequestDTO loginRequest) {
        // Aquí se busca al cliente por usuario y contraseña.
        // La clave es que 'encontrado' debe ser el cliente CORRECTO.
        Clientes encontrado = clientesDao.loginClientes(
                loginRequest.getUsuarioCliente(),
                loginRequest.getContrasenaCliente()
        );

        if (encontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        // DEBUG: Imprimir el ID del cliente que se está usando para generar el token
        System.out.println("DEBUG LOGIN: Usuario logueado: " + encontrado.getUsuarioCliente() +
                ", ID Cliente asociado: " + encontrado.getIdCliente() +
                ", Nombre: " + encontrado.getNombre());

        // Aquí se genera el token. El 'id_cliente' que se pone en el token es 'encontrado.getIdCliente()'
        String token = JwtUtil.generateToken(
                encontrado.getUsuarioCliente(),
                encontrado.getCorreo(),
                encontrado.getNombre(),
                encontrado.getIdCliente() // <--- ¡ESTE ES EL ID QUE VA EN EL TOKEN!
        );

        return ResponseEntity.ok(new ClienteLoginResponse(
                encontrado.getUsuarioCliente(),
                encontrado.getCorreo(),
                encontrado.getNombre(),
                token,
                encontrado.getIdCliente()
        ));
    }

    public static class ClienteLoginResponse {
        private String usuario, correo, nombre, token;
        private Integer idCliente;

        public ClienteLoginResponse(String usuario, String correo, String nombre, String token, Integer idCliente) {
            this.usuario = usuario;
            this.correo = correo;
            this.nombre = nombre;
            this.token = token;
            this.idCliente = idCliente;
        }

        public String getUsuario() { return usuario; }
        public String getCorreo() { return correo; }
        public String getNombre() { return nombre; }
        public String getToken() { return token; }
        public Integer getIdCliente() { return idCliente; }
    }
}
