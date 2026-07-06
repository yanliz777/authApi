package com.yfGomez.authApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing).
 *
 * ¿Qué es CORS?
 * Es una política de seguridad de los NAVEGADORES web. Cuando una página en
 * http://localhost:5173 (React/Vite) intenta hacer una petición HTTP a
 * http://localhost:8080 (Spring Boot), el navegador primero pregunta:
 * "¿Oye servidor, permites peticiones de este origen?"
 * Si el servidor no responde correctamente con las cabeceras CORS adecuadas,
 * el NAVEGADOR bloquea la respuesta y la aplicación no funciona.
 *
 * IMPORTANTE: CORS es una restricción del NAVEGADOR, no del servidor.
 * Por eso, Postman o curl siempre funcionan sin CORS (son herramientas, no navegadores).
 * Eso explica por qué nuestras pruebas con PowerShell funcionaron,
 * pero el React en el navegador no funcionaría sin esta configuración.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos: solo nuestro servidor de desarrollo de React.
        // En producción, esto cambiaría a la URL real del frontend.
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Métodos HTTP que el frontend puede usar
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Cabeceras que el frontend puede enviar (incluyendo Authorization para el JWT)
        config.setAllowedHeaders(List.of("*"));

        // Permite enviar cookies y la cabecera Authorization con credenciales
        config.setAllowCredentials(true);

        // Aplicar esta configuración a TODAS las rutas de la API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
