package com.BackNight.backendNIght;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager; // Añadir si lo usas
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Añadir si lo usas

import org.springframework.web.cors.CorsConfiguration; // <-- NUEVOS IMPORTS
import org.springframework.web.cors.CorsConfigurationSource; // <-- NUEVOS IMPORTS
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <-- NUEVOS IMPORTS
import java.util.Arrays; // <-- NUEVOS IMPORTS

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs sin sesiones
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- AÑADE ESTO para CORS global
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/servicio/login-cliente").permitAll()
                        .requestMatchers("/servicio/registrar-cliente").permitAll()
                        .requestMatchers("/servicio/**").permitAll() // Puedes mantenerlo para pruebas
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    // --- AÑADE ESTE BEAN PARA LA CONFIGURACIÓN GLOBAL DE CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Asegúrate de que esta URL sea EXACTAMENTE la de tu frontend.
        // Si tu frontend corre en http://localhost:5173, esto es correcto.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // Añade OPTIONS y HEAD
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos los headers
        configuration.setAllowCredentials(true); // Muy importante para credenciales (cookies, auth headers)

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