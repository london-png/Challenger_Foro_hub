package med.voll.ForoHub.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import med.voll.ForoHub.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

    //como optener secret de nuestra application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    // se crea para Confirmar que la clave secreta se cargó correctamente
    // Muestra el valor y longitud de la clave (útil en desarrollo)
    // Permite detectar problemas temprano (clave nula, demasiado corta, etc.)
    @PostConstruct
    public void init() {
        System.out.println("TokenService - Clave cargada: [" + secret + "]");
        System.out.println("Longitud de la clave: " + (secret != null ? secret.length() : 0) + " caracteres");
    }


    public String generarToken(Usuario usuario) {
        //generacion de nuestro token
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create() //sirve para crear
                    .withIssuer("forohub voll.med") //dice cual es el servidor que esta firmando ese token
                    .withSubject(usuario.getLogin()) // el usuario de quien va a recibir el token
                    .withExpiresAt(fechaExpiracion()) // fecha de expitacion del tokrn
                    .sign(algoritmo); // sirve para pasar el algoritmo
        } catch (JWTCreationException exception) {
            throw new RuntimeException("error al generar el token JWT", exception);
        }

    }

    //se crea el metodo para fechaExpiracion
    private Instant fechaExpiracion() {
        return LocalDateTime.now()
                .plusHours(2)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    //vamos a crear un metodo para obtener el subject de un token
    public String getSubJect(String tokenJWT) {

        //para validar un token
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("forohub voll.med")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();// obtiene subject si es el correcto
        } catch (JWTVerificationException exception) {
            System.err.println("ERROR DETALLADO AL VALIDAR TOKEN:");//*******
            System.err.println("   - Mensaje: " + exception.getMessage());//*****
            System.err.println("   - Causa: " + (exception.getCause() != null ? exception.getCause().getMessage() : "N/A"));//******
            throw new RuntimeException("Token JWT invalido o expirado!");

        }
    }

}