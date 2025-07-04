package com.BackNight.backendNIght;// package com.BackNight.backendNIght; // Asegúrate que el nombre del paquete sea correcto

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
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs sin sesiones
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Permite CORS a través del bean definido abajo
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API REST sin sesiones
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/servicio/login-cliente").permitAll()
                        .requestMatchers("/servicio/registrar-cliente").permitAll()
                        .requestMatchers("/servicio/**").permitAll() // Puedes ser más restrictivo después de probar
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                );
        return http.build();
    }

    // --- Configuración Global de CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // *** CAMBIO CLAVE AQUÍ: Asegúrate de añadir el dominio de tu frontend en Vercel ***
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",          // Para tu desarrollo local
                "https://nightplus.vercel.app"    // Para tu frontend desplegado en Vercel
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos los encabezados
        configuration.setAllowCredentials(true); // *** MUY IMPORTANTE: Permite el envío de credenciales/cookies ***

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