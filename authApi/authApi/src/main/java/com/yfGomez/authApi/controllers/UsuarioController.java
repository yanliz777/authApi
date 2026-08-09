package com.yfGomez.authApi.controllers;

import com.yfGomez.authApi.dtos.response.UsuarioResponse;
import com.yfGomez.authApi.entities.Usuario;
import com.yfGomez.authApi.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST Protegido para operaciones de Usuarios.
 * 
 * NOTA DE SEGURIDAD:
 * Todas las rutas bajo /api/v1/usuarios estan protegidas por el JwtAuthenticationFilter.
 * Si una petición llega SIN el Header 'Authorization: Bearer <token>', Spring responderá 401 Unauthorized.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Endpoint protegido para obtener la información del usuario autenticado actual.
     * Ruta: GET /api/v1/usuarios/me
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerMiPerfil() {
        // Leemos la información del usuario autenticado directamente del SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAutenticado = authentication.getName(); // Email extraído del JWT por el filtro

        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioResponse response = UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .creadoEn(usuario.getCreadoEn())
                .build();

        return ResponseEntity.ok(response);
    }
}
