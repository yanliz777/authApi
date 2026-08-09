package com.yfGomez.authApi.services.impl;

import com.yfGomez.authApi.dtos.request.LoginRequest;
import com.yfGomez.authApi.dtos.request.RegistroRequest;
import com.yfGomez.authApi.dtos.response.AuthResponse;
import com.yfGomez.authApi.dtos.response.UsuarioResponse;
import com.yfGomez.authApi.entities.Usuario;
import com.yfGomez.authApi.repositories.UsuarioRepository;
import com.yfGomez.authApi.security.JwtUtils;
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

    // Dependencias inyectadas por Spring IoC a través del constructor generado por Lombok
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils; // Inyectamos nuestro componente utilitario de JWT

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

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar al usuario en PostgreSQL por su email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Verificar que la contraseña en texto plano coincida con el hash BCrypt guardado en la BD
        // passwordEncoder.matches(textoPlano, hashEncriptado)
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generar el Token JWT firmado usando nuestro Bean JwtUtils
        String token = jwtUtils.generarToken(usuario.getEmail(), usuario.getRol());

        // 4. Retornar el DTO AuthResponse con el Token JWT y los datos públicos del usuario
        return AuthResponse.builder()
                .token(token)
                .tipoToken("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
}
