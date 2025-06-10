    package com.BackNight.backendNIght.ws.rest;

    import com.BackNight.backendNIght.ws.dao.AdministradoresDao;
    import com.BackNight.backendNIght.ws.entity.Administradores;
    import com.BackNight.backendNIght.ws.service.EmailService;
    import com.BackNight.backendNIght.ws.util.CodigoVerificacionStore;
    import com.BackNight.backendNIght.ws.util.JwtUtil;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.Random;

    @RestController
    @RequestMapping("/servicio")
    @CrossOrigin(origins = "http://localhost:5173")
    public class AdministradoresService {

        @Autowired private AdministradoresDao administradoresDao;
        @Autowired private EmailService emailService;
        @Autowired private CodigoVerificacionStore codigoStore;

        @PostMapping("/registrar-administrador")
        public ResponseEntity<?> registrarAdministrador(@RequestBody Administradores admin) {
            try {
                Administradores nuevoAdmin = administradoresDao.registrarAdministrador(admin);

                // Generar código de verificación
                String codigo = String.format("%06d", new Random().nextInt(999999));
                codigoStore.guardar(nuevoAdmin.getCorreoAdmin(), codigo);

                // Enviar email
                String contenido = "<h2>Bienvenido a Night +</h2><p>Código: <strong>" + codigo + "</strong></p>";
                emailService.enviarEmail(nuevoAdmin.getCorreoAdmin(), "Verificación Administrador", contenido);

                Map<String, String> res = new HashMap<>();
                res.put("mensaje", "Administrador registrado. Código enviado.");
                res.put("correo", nuevoAdmin.getCorreoAdmin());

                return ResponseEntity.ok(res);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al registrar: " + e.getMessage()));
            }
        }

        @GetMapping("/admins")
        public ResponseEntity<?> obtenerTodosLosAdministradores() {
            try {
                return ResponseEntity.ok(administradoresDao.obtenerTodos());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al obtener administradores: " + e.getMessage()));
            }
        }

        @PostMapping("/login-administrador")
        public ResponseEntity<?> loginAdministradores(@RequestBody Administradores administrador) {
            Administradores encontrado = administradoresDao.loginAdministradores(administrador.getUsuarioAdmin(), administrador.getContrasenaAdmin());
            if (encontrado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
            }

            String token = JwtUtil.generateToken(
                    encontrado.getUsuarioAdmin(),
                    encontrado.getCorreoAdmin(),
                    encontrado.getNombreAdmin()
            );

            return ResponseEntity.ok(new LoginResponse(
                    encontrado.getUsuarioAdmin(),
                    encontrado.getCorreoAdmin(),
                    encontrado.getNombreAdmin(),
                    token
            ));
        }

        @PostMapping("/verificar-codigo-admin")
        public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> payload) {
            String correo = payload.get("correo");
            String codigo = payload.get("codigo");

            if (!codigoStore.verificar(correo, codigo)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto.");
            }

            return ResponseEntity.ok("Código verificado.");
        }

        @PostMapping("/solicitar-recuperacion-admin")
        public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> payload) {
            String correo = payload.get("correo");

            Administradores admin = administradoresDao.obtenerTodos().stream()
                    .filter(a -> a.getCorreoAdmin().equalsIgnoreCase(correo))
                    .findFirst()
                    .orElse(null);

            if (admin == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado.");

            String codigo = String.format("%06d", new Random().nextInt(999999));
            codigoStore.guardar(correo, codigo);

            String html = "<p>Código de recuperación: <strong>" + codigo + "</strong></p>";
            emailService.enviarEmail(correo, "Recuperación de contraseña - Admin", html);

            return ResponseEntity.ok("Código enviado.");
        }

        @PostMapping("/cambiar-contrasena-admin")
        public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> payload) {
            String correo = payload.get("correo");
            String codigo = payload.get("codigo");
            String nueva = payload.get("nuevaContrasena");

            if (!codigoStore.verificar(correo, codigo)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto.");
            }

            Administradores admin = administradoresDao.obtenerTodos().stream()
                    .filter(a -> a.getCorreoAdmin().equalsIgnoreCase(correo))
                    .findFirst()
                    .orElse(null);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador no encontrado.");
            }

            admin.setContrasenaAdmin(nueva);
            administradoresDao.registrarAdministrador(admin);

            return ResponseEntity.ok("Contraseña actualizada.");
        }

        // Clase para enviar la respuesta del login
        public static class LoginResponse {
            private String usuario, correo, nombre, token;

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
