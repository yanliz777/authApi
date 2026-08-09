package com.yfGomez.authApi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Componente de Utilidad para JWT (@Component).
 * 
 * ¿Por qué @Component?
 * Porque es un Bean reutilizable gestionado por Spring IoC que será inyectado
 * en la capa de servicios (AuthServiceImpl) y en la capa de filtros (JwtAuthenticationFilter).
 */
@Component
public class JwtUtils {

    // Leemos la clave secreta y la expiración desde application.properties (.env)
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Convierte la cadena del secretKey en una clave criptográfica SecretKey HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un Token JWT firmado para un usuario.
     *
     * @param email El correo del usuario (Subject/Asunto del token).
     * @param rol   El rol del usuario ("USER", "ADMIN").
     * @return Cadena JWT compacta firmada.
     */
    public String generarToken(String email, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);

        // Usamos el patrón Builder de la librería JJWT para construir el token paso a paso:
        return Jwts.builder()
                .subject(email)            // <-- Define el Claim registrado "sub" (Identificador principal del usuario)
                .claim("rol", rol)         // <-- Define nuestro Claim personalizado "rol" (USER / ADMIN)
                .issuedAt(ahora)           // <-- Define el Claim registrado "iat" (Fecha de emisión en timestamp)
                .expiration(expiracion)    // <-- Define el Claim registrado "exp" (Fecha de expiración en timestamp)
                .signWith(getSigningKey()) // <-- Genera la Firma Digital HMAC-SHA256 usando nuestra clave secreta del .env
                .compact();                // <-- Convierte todo en la cadena final separada por puntos (Header.Payload.Signature)
    }

    /**
     * Extrae el email (Subject) guardado dentro de un token JWT.
     */
    public String obtenerEmailDelToken(String token) {
        return obtenerClaim(token, Claims::getSubject);
    }

    /**
     * Método genérico para extraer cualquier Claim (propiedad) del token.
     */
    public <T> T obtenerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Desempaqueta y verifica la firma del Token JWT.
     * Lanza una excepción automáticamente si el token fue alterado.
     */
    private Claims obtenerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Verifica la firma con nuestra clave secreta
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida si el token le pertenece al usuario y si aún no ha expirado.
     */
    public boolean esTokenValido(String token, String email) {
        final String emailDelToken = obtenerEmailDelToken(token);
        return (emailDelToken.equals(email) && !esTokenExpirado(token));
    }

    /**
     * Verifica si la fecha de expiración del token ya pasó.
     */
    private boolean esTokenExpirado(String token) {
        return obtenerClaim(token, Claims::getExpiration).before(new Date());
    }
}
