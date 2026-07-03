package com.yfGomez.authApi.services.impl;

import com.yfGomez.authApi.dtos.request.RegistroRequest;
import com.yfGomez.authApi.dtos.response.UsuarioResponse;
import com.yfGomez.authApi.entities.Usuario;
import com.yfGomez.authApi.repositories.UsuarioRepository;
import com.yfGomez.authApi.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación de la lógica de negocio para autenticación.
 * La anotación @RequiredArgsConstructor de Lombok genera automáticamente un constructor
 * con todos los campos declarados como 'final', realizando la Inyección de Dependencias.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Dependencias inyectadas (relación de Dependencia POO)
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrarUsuario(RegistroRequest request) {
        // 1. Validar si el email ya existe (Regla de negocio)
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            // Lanza un error genérico por ahora. Más adelante crearemos excepciones personalizadas (ej: ConflictException)
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // 2. Mapear DTO -> Entidad y Encriptar la contraseña (JAMÁS guardar contraseñas en texto plano)
        Usuario nuevoUsuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encriptación con BCrypt
                .rol("USER") // Rol por defecto
                .activo(true) // Usuario activo por defecto
                .build();

        // 3. Guardar en Base de Datos
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Mapear Entidad -> DTO de respuesta (para no enviar la contraseña ni datos sensibles al cliente)
        return UsuarioResponse.builder()
                .id(usuarioGuardado.getId())
                .nombre(usuarioGuardado.getNombre())
                .email(usuarioGuardado.getEmail())
                .rol(usuarioGuardado.getRol())
                .activo(usuarioGuardado.getActivo())
                .creadoEn(usuarioGuardado.getCreadoEn())
                .build();
    }
}
