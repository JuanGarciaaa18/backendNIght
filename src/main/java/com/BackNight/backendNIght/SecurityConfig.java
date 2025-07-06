package com.BackNight.backendNIght;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs sin sesiones (típico en REST APIs)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS usando la configuración de abajo
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API REST sin sesiones de estado
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a estas rutas específicas
                        .requestMatchers("/servicio/login-cliente").permitAll()
                        .requestMatchers("/servicio/registrar-cliente").permitAll()
                        .requestMatchers("/servicio/create-mercadopago-preference").permitAll() // <-- CLAVE: Permitir esta ruta
                        .requestMatchers("/servicio/mercadopago/webhook").permitAll() // CLAVE: Permitir el webhook de MP
                        .requestMatchers("/servicio/confirmar-reserva").permitAll() // CLAVE: Permitir la confirmación de reserva
                        // Puedes ser más específico con otras rutas si no quieres que todo "/servicio/**" sea público
                        // Si estas rutas son públicas, no necesitan token JWT
                        .requestMatchers("/servicio/evento/**").permitAll()
                        .requestMatchers("/servicio/eventos-list").permitAll()
                        .requestMatchers("/servicio/eventos-por-discoteca/**").permitAll()
                        // Cualquier otra ruta que empiece con /servicio requerirá autenticación si no está en la lista de .permitAll()
                        .requestMatchers("/servicio/admin/**").authenticated() // Ejemplo: estas sí requieren autenticación
                        .requestMatchers("/servicio/guardar-evento").authenticated()
                        .requestMatchers("/servicio/actualizar-evento").authenticated()
                        .requestMatchers("/servicio/eliminar-evento/**").authenticated()
                        .anyRequest().authenticated() // Cualquier otra solicitud que no sea /servicio/** requiere autenticación
                );
        return http.build();
    }

    // --- Configuración Global de CORS ---
    // Esta configuración reemplaza y centraliza cualquier @CrossOrigin en los controladores
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // *** ESENCIAL: Asegúrate de que los ORÍGENES sean EXACTAMENTE los de tus frontends ***
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",          // Para tu entorno de desarrollo local (Vite/React por defecto)
                "http://127.0.0.1:5173",          // A veces localhost se resuelve a esta IP
                "https://nightplus.vercel.app"    // <-- ¡Este es el dominio CRÍTICO de Vercel para tu PROD!
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // Incluye OPTIONS para pre-flight requests
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos los headers (incluyendo Authorization)
        configuration.setAllowCredentials(true); // Necesario si envías cookies/headers de autorización (como Bearer Token)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a TODAS las rutas de tu API
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}