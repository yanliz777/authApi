package com.yfGomez.authApi.repositories;

import com.yfGomez.authApi.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Usuario.
 * 
 * ¿Por qué una interfaz? 
 * Spring Data JPA crea la implementación de esta interfaz automáticamente en tiempo de ejecución.
 * Al extender JpaRepository<Usuario, Long>, ya tenemos métodos como save(), findById(), delete(), etc.,
 * sin tener que escribir ni una sola línea de SQL.
 */
@Repository // Indica que esta interfaz es un componente de acceso a datos de Spring.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data es tan inteligente que con solo nombrar el método siguiendo la convención,
    // genera la consulta SQL: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // Método para validar si un email ya existe en la BD:
    // SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?
    boolean existsByEmail(String email);

}
