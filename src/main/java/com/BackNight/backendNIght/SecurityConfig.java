package com.BackNight.backendNIght; // Asegúrate de que este sea tu paquete correcto

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
import java.util.List; // Importar List

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas permitidas para acceso público (sin autenticación JWT)
                        .requestMatchers("/servicio/login-cliente").permitAll()
                        .requestMatchers("/servicio/registrar-cliente").permitAll()
                        .requestMatchers("/servicio/create-mercadopago-preference").permitAll()
                        .requestMatchers("/servicio/mercadopago/webhook").permitAll()
                        .requestMatchers("/servicio/confirmar-reserva").permitAll()
                        .requestMatchers("/servicio/evento/**").permitAll()
                        .requestMatchers("/servicio/eventos-list").permitAll()
                        .requestMatchers("/servicio/eventos-por-discoteca/**").permitAll()
                        // Rutas que requieren autenticación
                        .requestMatchers("/servicio/admin/**").authenticated()
                        .requestMatchers("/servicio/guardar-evento").authenticated()
                        .requestMatchers("/servicio/actualizar-evento").authenticated()
                        .requestMatchers("/servicio/eliminar-evento/**").authenticated()
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "https://nightplus.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*")); // Utiliza List.of para mayor simplicidad
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Opcional: tiempo de caché para respuestas preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
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