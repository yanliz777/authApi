package com.yfGomez.authApi.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) para recibir los datos de registro.
 * Usar DTOs evita vulnerabilidades como el "Mass Assignment", donde un usuario malicioso
 * podría enviar un JSON con "rol": "ADMIN" y darnos problemas si usáramos la entidad directamente.
 */
@Data // Genera getters y setters
public class RegistroRequest {

    // @NotBlank verifica que el campo no venga nulo ni vacío ("")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido") // Valida formato email (ej: a@a.com)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

}
