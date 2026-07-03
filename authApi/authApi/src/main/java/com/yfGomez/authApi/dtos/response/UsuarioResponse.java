package com.yfGomez.authApi.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para enviar la información del usuario de vuelta al cliente.
 * NOTA: Nunca incluimos la contraseña aquí.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
    private LocalDateTime creadoEn;

}
