package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.util.JwtUtil;
import com.BackNight.backendNIght.ws.dao.ClientesDao;
import com.BackNight.backendNIght.ws.entity.Clientes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Asegúrate de que esta dependencia está en tu pom.xml
import org.springframework.web.bind.annotation.*;
import com.BackNight.backendNIght.ws.service.EmailService;
import com.BackNight.backendNIght.ws.util.CodigoVerificacionStore;
import com.BackNight.backendNIght.ws.dto.ClienteLoginRequestDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Importar Optional
import java.util.Random;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ClientesService { // Un nombre más común para un controlador es ClientesController

    @Autowired
    private ClientesDao clientesDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodigoVerificacionStore codigoStore;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Utilizado para cifrar contraseñas


    // Método auxiliar para extraer el ID del cliente del token
    private Integer getClienteIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                return JwtUtil.extractIdUsuarioFromToken(token); // Asume que este método devuelve el ID del cliente
            } catch (Exception e) {
                System.err.println("Error al extraer ID de cliente del token: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @PostMapping("/registrar-cliente")
    public ResponseEntity<?> registrarCliente(@RequestBody Clientes cliente) {
        try {
            // **¡IMPORTANTE!:** ELIMINA ESTA LÍNEA DE CIFRADO AQUÍ.
            // Tu DAO (clientesDao.registrarCliente) ya cifra la contraseña.
            // Si la dejas aquí, la contraseña se cifrará dos veces, lo cual es incorrecto.
            // cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));

            Clientes nuevoCliente = clientesDao.registrarCliente(cliente); // El DAO ya cifra la contraseña

            String codigo = String.format("%06d", new Random().nextInt(999999));
            codigoStore.guardar(nuevoCliente.getCorreo(), codigo);

            // Contenido HTML para el correo de verificación
            String contenidoHtml = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #1a1a1a; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); padding: 30px; text-align: center;'>" +
                    "<img src='https://i.imgur.com/rrMQaVZ.png' alt='Night +' style='width: 120px; margin-bottom: 20px;' />" +
                    "<h2 style='color: #6A1B9A; font-size: 26px;'>¡Bienvenido a <span style='color:#6A1B9A;'>Night +</span>!</h2>" +
                    "<p style='font-size: 16px; color: #333;'>Gracias por registrarte. Usa el siguiente código para verificar tu cuenta:</p>" +
                    "<p style='font-size: 32px; font-weight: bold; color: #2196F3; margin: 20px 0;'>" + codigo + "</p>" +
                    "<p style='font-size: 14px; color: #777;'>Si no solicitaste este código, puedes ignorar este correo.</p>" +
                    "<p style='font-size: 14px; color: #555; margin-top: 30px;'>Atentamente,<br><strong>El equipo de Night +</strong></p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            emailService.enviarEmail(nuevoCliente.getCorreo(), "Código de verificación", contenidoHtml);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente registrado. Código enviado.");
            response.put("correo", nuevoCliente.getCorreo());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // En un entorno de producción, usa un logger (ej. SLF4J con Logback).
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar cliente: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarCliente(@RequestBody Clientes clienteActualizado, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Integer idClienteToken = getClienteIdFromAuthHeader(authorizationHeader);
            if (idClienteToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado: Token inválido o ausente.");
            }

            // Obtener el cliente existente de la base de datos por el ID del token
            Optional<Clientes> clienteExistenteOpt = clientesDao.findById(idClienteToken);
            if (clienteExistenteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado para el ID del token.");
            }
            Clientes clienteExistente = clienteExistenteOpt.get();

            // Actualizar solo los campos que se permiten modificar
            if (clienteActualizado.getNombre() != null && !clienteActualizado.getNombre().isBlank()) {
                clienteExistente.setNombre(clienteActualizado.getNombre());
            }
            if (clienteActualizado.getEdad() != null) {
                clienteExistente.setEdad(clienteActualizado.getEdad());
            }
            if (clienteActualizado.getTelefono() != null && !clienteActualizado.getTelefono().isBlank()) {
                clienteExistente.setTelefono(clienteActualizado.getTelefono());
            }
            // El correo NO DEBERÍA poder cambiarse así sin un proceso de verificación adicional
            // Si permites cambiar el correo, considera un proceso de re-verificación de email.
            // Por ahora, lo mantenemos como el existente.
            // if (clienteActualizado.getCorreo() != null && !clienteActualizado.getCorreo().isBlank()) {
            //     clienteExistente.setCorreo(clienteActualizado.getCorreo());
            // }

            // La contraseña solo se actualiza si se envía una nueva en el request
            if (clienteActualizado.getContrasenaCliente() != null && !clienteActualizado.getContrasenaCliente().isBlank()) {
                clienteExistente.setContrasenaCliente(passwordEncoder.encode(clienteActualizado.getContrasenaCliente()));
            }
            // El usuarioCliente no debería ser actualizado por el cliente mismo en este endpoint
            // si el nombre de usuario es un identificador único y permanente.
            // Si lo permites, asegúrate de validar que no haya duplicados.

            Clientes clienteActualizadoDB = clientesDao.actualizarCliente(clienteExistente);

            return ResponseEntity.ok(clienteActualizadoDB);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");

        Clientes cliente = clientesDao.obtenerPorCorreo(correo);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado.");
        }

        String codigo = String.format("%06d", new Random().nextInt(999999));
        codigoStore.guardar(correo, codigo);

        String html = "<p>Tu código de recuperación es: <strong>" + codigo + "</strong></p>";
        emailService.enviarEmail(correo, "Recuperación de contraseña - Night+", html);

        return ResponseEntity.ok("Código de recuperación enviado.");
    }

    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String codigo = payload.get("codigo");
        String nuevaContrasena = payload.get("nuevaContrasena");

        if (!codigoStore.verificar(correo, codigo)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto.");
        }

        Clientes cliente = clientesDao.obtenerPorCorreo(correo);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        clientesDao.actualizarContrasena(cliente, nuevaContrasena);

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String codigoIngresado = payload.get("codigo");

        if (correo == null || codigoIngresado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Correo y código son obligatorios.");
        }

        String codigoGuardado = codigoStore.obtener(correo);

        if (codigoGuardado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró un código para este correo.");
        }

        if (codigoGuardado.trim().equals(codigoIngresado.trim())) {
            return ResponseEntity.ok("Código verificado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto.");
        }
    }

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginClientes(@Valid @RequestBody ClienteLoginRequestDTO loginRequest) {
        Clientes encontrado = clientesDao.loginClientes(
                loginRequest.getUsuarioCliente(),
                loginRequest.getContrasenaCliente()
        );

        if (encontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = JwtUtil.generateToken(
                encontrado.getUsuarioCliente(),
                encontrado.getCorreo(),
                encontrado.getNombre(),
                encontrado.getIdCliente()
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
