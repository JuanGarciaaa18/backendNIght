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

import java.util.HashMap;
import java.util.List;
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
            String contenidoHtml = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #1a1a1a; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); padding: 30px; text-align: center;'>" +

                    // Logo
                    "<img src='https://i.imgur.com/rrMQaVZ.png' alt='Night +' style='width: 120px; margin-bottom: 20px;' />" +

                    // Título
                    "<h2 style='color: #6A1B9A; font-size: 26px;'>¡Bienvenido a <span style='color:#6A1B9A;'>Night +</span>!</h2>" +

                    // Mensaje principal
                    "<p style='font-size: 16px; color: #333;'>Gracias por registrarte. Usa el siguiente código para verificar tu cuenta:</p>" +
                    "<p style='font-size: 32px; font-weight: bold; color: #2196F3; margin: 20px 0;'>" + codigo + "</p>" +

                    // Nota adicional
                    "<p style='font-size: 14px; color: #777;'>Si no solicitaste este código, puedes ignorar este correo.</p>" +

                    // Firma
                    "<p style='font-size: 14px; color: #555; margin-top: 30px;'>Atentamente,<br><strong>El equipo de Night +</strong></p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";


            emailService.enviarEmail(
                    nuevoCliente.getCorreo(),
                    "Código de verificación",
                    contenidoHtml
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

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");

        // Buscar cliente por correo
        List<Clientes> clientes = clientesDao.obtenerTodos(); // tendrás que implementar este método
        Clientes cliente = clientes.stream()
                .filter(c -> c.getCorreo().equalsIgnoreCase(correo))
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado.");
        }

        // Generar código
        String codigo = String.format("%06d", new Random().nextInt(999999));
        codigoStore.guardar(correo, codigo);

        // Enviar email
        String html = "<p>Tu código de recuperación es: <strong>" + codigo + "</strong></p>";
        emailService.enviarEmail(correo, "Recuperación de contraseña - Night+", html);

        return ResponseEntity.ok("Código de recuperación enviado.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarCliente(@RequestBody Clientes cliente) {
        try {
            // Validar que el correo no sea nulo
            if (cliente.getCorreo() == null || cliente.getCorreo().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo es obligatorio.");
            }

            // Buscar cliente actual
            Clientes clienteExistente = clientesDao.obtenerPorCorreo(cliente.getCorreo());
            if (clienteExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado.");
            }

            // Evitar actualizar el usuario_cliente
            cliente.setUsuarioCliente(clienteExistente.getUsuarioCliente());

            // Si la contraseña viene vacía, mantener la anterior
            if (cliente.getContrasenaCliente() == null || cliente.getContrasenaCliente().isEmpty()) {
                cliente.setContrasenaCliente(clienteExistente.getContrasenaCliente());
            } else {
                // Encriptar nueva contraseña
                cliente.setContrasenaCliente(new BCryptPasswordEncoder().encode(cliente.getContrasenaCliente()));
            }

            // Asignar el ID correcto
            cliente.setIdCliente(clienteExistente.getIdCliente());

            // Actualizar cliente
            Clientes actualizado = clientesDao.registrarCliente(cliente);
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el cliente.");
            }

            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error actualizando cliente: " + e.getMessage());
        }
    }
    @PostMapping("/cambiar-contrasena")  // POST, no PUT
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
    public ResponseEntity<?> loginCliente(@RequestBody Clientes cliente) {
        Clientes clienteEncontrado = clientesDao.loginCliente(cliente.getUsuarioCliente(), cliente.getContrasenaCliente());
        if (clienteEncontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = JwtUtil.generateToken(
                clienteEncontrado.getUsuarioCliente(),
                clienteEncontrado.getCorreo(),
                clienteEncontrado.getNombre()
        );

        return ResponseEntity.ok(new LoginResponse(clienteEncontrado.getUsuarioCliente(), clienteEncontrado.getCorreo(), clienteEncontrado.getNombre(), token));
    }

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