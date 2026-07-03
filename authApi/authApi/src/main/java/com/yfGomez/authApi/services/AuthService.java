package com.yfGomez.authApi.services;

import com.yfGomez.authApi.dtos.request.RegistroRequest;
import com.yfGomez.authApi.dtos.response.UsuarioResponse;

/**
 * Interfaz que define las operaciones permitidas para la autenticación y registro.
 * Usar interfaces nos permite tener un bajo acoplamiento y facilita el "mocking" en las pruebas unitarias.
 */
public interface AuthService {
    
    UsuarioResponse registrarUsuario(RegistroRequest request);
    
}
