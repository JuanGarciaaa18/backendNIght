package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.util.JwtUtil;
import com.BackNight.backendNIght.ws.dao.ClientesDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.BackNight.backendNIght.ws.service.EmailService;
import com.BackNight.backendNIght.ws.util.CodigoVerificacionStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "http://localhost:5173")
public class ClientesService {

    @Autowired
    private ClientesDao clientesDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodigoVerificacionStore codigoStore;

    @PostMapping("/registrar-cliente")
    public ResponseEntity<?> registrarCliente(@RequestBody Clientes cliente) {
        try {
            Clientes nuevoCliente = clientesDao.registrarCliente(cliente);

            // Generar código de verificación
            String codigo = String.format("%06d", new Random().nextInt(999999));

            // Guardar código
            codigoStore.guardar(nuevoCliente.getCorreo(), codigo);

            // Enviar código por correo
            emailService.enviarEmail(
                    nuevoCliente.getCorreo(),
                    "Código de verificación",
                    "Tu código de verificación es: " + codigo
            );

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente registrado. Código enviado.");
            response.put("correo", nuevoCliente.getCorreo());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar cliente: " + e.getMessage());
        }
    }


    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody Clientes cliente) {
        Clientes clienteEncontrado = clientesDao.loginCliente(cliente.getUsuarioCliente(), cliente.getContrasenaCliente());
        if (clienteEncontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = JwtUtil.generateToken(
                clienteEncontrado.getUsuarioCliente(),
                clienteEncontrado.getCorreo(),
                clienteEncontrado.getNombre() // ✅ Asegúrate de que no sea null
        );



        return ResponseEntity.ok(new LoginResponse(clienteEncontrado.getUsuarioCliente(), clienteEncontrado.getCorreo(),clienteEncontrado.getNombre() ,token));
    }


    // Clase interna o pública (puedes moverla a un paquete dto)
    public static class LoginResponse {
        private String usuario;
        private String correo;
        private String nombre;
        private String token;

        public LoginResponse(String usuario, String correo, String nombre, String token) {
            this.usuario = usuario;
            this.correo = correo;
            this.nombre = nombre;
            this.token = token;
        }

        public String getUsuario() { return usuario; }
        public String getCorreo() { return correo; }
        public String getNombre() { return nombre; }
        public String getToken() { return token; }
    }



}
