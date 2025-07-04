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
import jakarta.validation.Valid; // Importar para validación de DTOs

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/servicio")
@CrossOrigin(origins = "https://nightplus.vercel.app")
public class ClientesService { // Considera renombrar a ClientesController

    @Autowired
    private ClientesDao clientesDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodigoVerificacionStore codigoStore;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/registrar-cliente")
    public ResponseEntity<?> registrarCliente(@RequestBody Clientes cliente) {
        try {
            // ¡¡IMPORTANTE: ELIMINA ESTA LÍNEA!!
            // Tu DAO (clientesDao.registrarCliente) ya cifra la contraseña.
            // Si la dejas aquí, la contraseña se cifrará dos veces, lo cual es incorrecto.
            // cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));

            // El DAO se encarga de cifrar la contraseña antes de guardar.
            Clientes nuevoCliente = clientesDao.registrarCliente(cliente);

            String codigo = String.format("%06d", new Random().nextInt(999999));
            codigoStore.guardar(nuevoCliente.getCorreo(), codigo);

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
            e.printStackTrace(); // Usa un logger en lugar de printStackTrace en producción.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar cliente: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarCliente(@RequestBody Clientes cliente) {
        try {
            if (cliente.getCorreo() == null || cliente.getCorreo().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo es obligatorio.");
            }

            // Si el repositorio devuelve Optional<Clientes>, harías:
            // Optional<Clientes> clienteExistenteOpt = clientesDao.obtenerPorCorreo(cliente.getCorreo());
            // if (clienteExistenteOpt.isEmpty()) { ... }
            // Clientes clienteExistente = clienteExistenteOpt.get();
            Clientes clienteExistente = clientesDao.obtenerPorCorreo(cliente.getCorreo());
            if (clienteExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado.");
            }

            // Asegurar que el ID y el usuario se mantengan del cliente existente
            cliente.setIdCliente(clienteExistente.getIdCliente());
            // Solo si no quieres que el cliente actualice su usuario_cliente desde el request:
            cliente.setUsuarioCliente(clienteExistente.getUsuarioCliente());


            if (cliente.getContrasenaCliente() != null && !cliente.getContrasenaCliente().isBlank()) {
                // Correcto: Hashea la nueva contraseña aquí en el servicio antes de pasarla al DAO.
                cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
            } else {
                // Si la contraseña no se envía en el request, se mantiene la existente.
                cliente.setContrasenaCliente(clienteExistente.getContrasenaCliente());
            }

            // Comenta o elimina esta línea en producción:
            System.out.println("Contraseña que se guardará (hasheada): " + cliente.getContrasenaCliente());

            Clientes actualizado = clientesDao.actualizarCliente(cliente);

            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            e.printStackTrace(); // Usar logger.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");

        // ¡OPTIMIZACIÓN CRÍTICA! Usar directamente el método que busca por correo.
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

        // Correcto: El método `actualizarContrasena` del DAO ya se encarga de cifrar
        // la `nuevaContrasena` (que llega aquí en texto plano).
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
        // La lógica de verificación de contraseña hasheada está en el DAO, lo cual es correcto.
        // Aquí no necesitas usar passwordEncoder.matches() directamente.
        Clientes encontrado = clientesDao.loginClientes(
                loginRequest.getUsuarioCliente(),
                loginRequest.getContrasenaCliente() // Esta es la contraseña en texto plano
        );

        if (encontrado == null) {
            // Este mensaje se devuelve si el usuario no existe O si la contraseña es incorrecta
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

    // Clase para enviar la respuesta del login del cliente
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

        // Getters para los campos
        public String getUsuario() { return usuario; }
        public String getCorreo() { return correo; }
        public String getNombre() { return nombre; }
        public String getToken() { return token; }
        public Integer getIdCliente() { return idCliente; }
    }
}