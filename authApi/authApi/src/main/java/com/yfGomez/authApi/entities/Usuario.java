package com.yfGomez.authApi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Usuario que mapea directamente a la tabla "usuarios" en PostgreSQL.
 * Usamos Lombok (@Data, @Builder, etc.) para generar automáticamente los getters, setters y constructores,
 * manteniendo el código limpio.
 */
@Entity
@Table(name = "usuarios")
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor vacío (requerido por Hibernate)
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Builder // Permite crear instancias usando el patrón Builder (Usuario.builder().nombre("x").build())
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // La contraseña siempre debe estar encriptada en la base de datos (con BCrypt)
    @Column(nullable = false)
    private String password;

    // Rol del usuario: "USER" o "ADMIN"
    @Column(nullable = false, length = 20)
    private String rol;

    // Para "Soft Delete" (marcar como inactivo en vez de borrar)
    @Column(nullable = false)
    private Boolean activo;

    // @CreationTimestamp hace que Hibernate llene este campo automáticamente al insertar
    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // @UpdateTimestamp hace que Hibernate actualice este campo automáticamente al modificar
    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

}
