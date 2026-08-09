package com.yfGomez.authApi.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) devuelto tras un inicio de sesión exitoso.
 * Contiene el Token JWT y la información pública del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    
    @Builder.Default
    private String tipoToken = "Bearer";

    private Long id;
    private String nombre;
    private String email;
    private String rol;
}
