package com.BackNight.backendNIght; // Asegúrate de que el paquete sea correcto

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
import java.util.Arrays; // Importa Arrays

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs sin sesiones (típico en REST APIs)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- ¡Esta línea es CLAVE para habilitar CORS!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API REST sin sesiones de estado
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/servicio/login-cliente").permitAll()
                        .requestMatchers("/servicio/registrar-cliente").permitAll()
                        // Puedes ser más específico aquí si solo algunas rutas requieren permiso:
                        .requestMatchers("/servicio/create-mercadopago-preference").permitAll() // <-- Asegura que esta ruta esté permitida
                        .requestMatchers("/servicio/**").permitAll() // Esto permite todas las rutas bajo /servicio
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                );
        return http.build();
    }

    // --- Configuración Global de CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // *** Asegúrate de que los ORÍGENES sean EXACTAMENTE los de tus frontends ***
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",          // Para tu entorno de desarrollo local
                "https://nightplus.vercel.app"    // <-- ¡Este es el dominio crítico de Vercel!
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // Incluye OPTIONS
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos los encabezados
        configuration.setAllowCredentials(true); // Necesario si envías cookies, tokens de auth, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a TODAS las rutas
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