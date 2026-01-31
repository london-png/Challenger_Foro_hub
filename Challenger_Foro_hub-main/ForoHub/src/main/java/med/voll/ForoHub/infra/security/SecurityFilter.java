package med.voll.ForoHub.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.ForoHub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 🔍 LOG 1: Ver qué llega en el header Authorization
        var authorizationHeader = request.getHeader("Authorization");
        System.out.println("🔍 Header Authorization recibido: " + authorizationHeader);

        // 🔍 LOG 2: Ver la URL de la petición
        System.out.println("🔍 URL de la petición: " + request.getMethod() + " " + request.getRequestURI());

        // Recuperar token
        var tokenJWT = recuperarToken(request);

        // 🔍 LOG 3: Ver el token extraído
        if (tokenJWT != null) {
            System.out.println("🔍 Token extraído (primeros 50 chars): " +
                    (tokenJWT.length() > 50 ? tokenJWT.substring(0, 50) + "..." : tokenJWT));
            System.out.println("🔍 Longitud del token: " + tokenJWT.length() + " caracteres");
        } else {
            System.out.println("⚠️ Token es NULL o vacío");
        }

        if (tokenJWT == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var subject = tokenService.getSubJect(tokenJWT);
            var usuario = usuarioRepository.findByLogin(subject);

            if (usuario == null) {
                System.err.println("❌ Usuario no encontrado: " + subject);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    usuario,
                    null,
                    usuario.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ Usuario autenticado: " + subject);

        } catch (Exception e) {
            System.err.println("❌ ERROR AL VALIDAR TOKEN: " + e.getMessage());
            e.printStackTrace();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido o expirado. Por favor, inicia sesión nuevamente."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}