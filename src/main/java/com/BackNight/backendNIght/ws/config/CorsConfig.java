package com.BackNight.backendNIght.ws.config; // Puedes ajustar el paquete

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/servicio/**") // Aplica CORS a todos los endpoints bajo /servicio
                .allowedOrigins("https://nightplus.vercel.app") // Permite solicitudes desde tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todas las cabeceras en las solicitudes
                .allowCredentials(true) // Permite el envío de cookies, cabeceras de autorización, etc.
                .maxAge(3600); // Tiempo en segundos que la respuesta de pre-vuelo puede ser cacheada
    }
}
