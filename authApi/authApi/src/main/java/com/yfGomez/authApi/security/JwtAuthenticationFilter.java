package com.yfGomez.authApi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de Seguridad Interceptor (@Component).
 * 
 * Hereda de OncePerRequestFilter para garantizar que se ejecute exactamente UNA VEZ 
 * por cada petición HTTP que llega a nuestro servidor.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer la cabecera Authorization ("Authorization: Bearer eyJhbGci...")
        final String authHeader = request.getHeader("Authorization");

        // Si no hay cabecera o no empieza por "Bearer ", continua con los demás filtros (rutas públicas)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer el token puro cortando la palabra "Bearer " (7 caracteres) y eliminando espacios adicionales
            final String jwt = authHeader.substring(7).trim();
            final String userEmail = jwtUtils.obtenerEmailDelToken(jwt);

            // 3. Si hay email y el usuario AÚN no ha sido autenticado en el contexto de la petición actual
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 4. Validar autenticidad de la firma y expiración del Token
                if (jwtUtils.esTokenValido(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Establecer la autenticación del usuario en el SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si el token expiro, es invalido o fue alterado, capturamos el error
            // al no llenar el SecurityContext, Spring Security denegara el acceso con 401
            logger.error("Error al procesar el Token JWT: " + e.getMessage());
        }

        // 6. Pasar la petición al controlador correspondiente
        filterChain.doFilter(request, response);
    }
}
