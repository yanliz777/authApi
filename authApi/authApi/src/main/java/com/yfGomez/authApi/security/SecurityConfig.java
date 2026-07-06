package com.yfGomez.authApi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración central de seguridad.
 * Aquí definimos qué rutas son públicas, cuáles privadas, y cómo manejamos sesiones.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean que expone el encriptador de contraseñas.
     * BCrypt es el estándar actual para hashear passwords de manera segura.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean principal que configura la cadena de filtros HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            // Deshabilitamos CSRF porque nuestra API será REST (no usamos cookies/formularios HTML clásicos)
            .csrf(AbstractHttpConfigurer::disable)
            // Habilitamos CORS usando el Bean CorsConfig que definimos en config/CorsConfig.java
            // Sin esto, el navegador bloqueará las peticiones desde React (localhost:5173)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Configuramos las políticas de autorización para las rutas
            .authorizeHttpRequests(auth -> auth
                // La ruta de registro y login deben ser públicas
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Cualquier otra ruta requerirá estar autenticado
                .anyRequest().authenticated()
            )
            
            // Le decimos a Spring Security que no cree sesiones HTTP en el servidor, 
            // ya que usaremos JWT (Stateless = sin estado). Cada petición deberá traer su token.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
