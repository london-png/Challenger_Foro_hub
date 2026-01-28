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

@Component // es para algo generico que necesitimos que spring lo cargue

// va ejecutar cada uno de las  request que reciba nuestro backend
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //creamos la instancia TokenService
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //System.out.println("se llamo el filter");
        //recibimos el token del usuario y validarlo
        var tokenJWT = recuperarToken(request);
        if (tokenJWT !=null){
            var subject = tokenService.getSubJect(tokenJWT);
            var usuario = usuarioRepository.findByLogin(subject);

            var authemtication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

            //con este metodo le decimos a Spring security que realmente este usuario esta autenticado
            SecurityContextHolder.getContext().setAuthentication(authemtication);
        }

        //el FilterChain es la cadena de filtros que contiene y nos dice cual es el siguiente filtro que tenemos que llamar
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null) {
            //vamos a remplazar el Bearer, por un caracter vacio
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;

    }
}
