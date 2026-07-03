package com.yfGomez.authApi.controllers;

import com.yfGomez.authApi.dtos.request.RegistroRequest;
import com.yfGomez.authApi.dtos.response.UsuarioResponse;
import com.yfGomez.authApi.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar todo lo relacionado con autenticación (Registro, Login, etc).
 * 
 * @RestController: Indica que todos los métodos devolverán datos (JSON) y no vistas HTML.
 * @RequestMapping("/api/v1/auth"): Define la ruta base para este controlador.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // Inyectamos el servicio (la capa de negocio)
    private final AuthService authService;

    /**
     * Endpoint para registrar un nuevo usuario.
     * Método HTTP: POST
     * Ruta completa: POST /api/v1/auth/registro
     * 
     * @param request El JSON del frontend mapeado al objeto RegistroRequest. @Valid fuerza que se cumplan las validaciones (@NotBlank, @Email, etc).
     * @return ResponseEntity con el usuario creado y status 201 (CREATED).
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        
        // El controlador NO tiene lógica de negocio. Solo delega al servicio.
        UsuarioResponse nuevoUsuario = authService.registrarUsuario(request);
        
        // Retornamos la respuesta con el código HTTP 201 (Created) que es la mejor práctica para POST
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
}
