package med.voll.ForoHub.infra.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity //habilita la seguridad
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;
    //crear un metodo que sea capas de sacar las configuraciones de Spring Security

    @Bean //indica que esta clase puede ser cargada para que posteriormente se pueda usar
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable()) //deshabilitamos la seguridad por que usamos un sistema stateless y ya estamos protegidos

                //debemos convertir el sistema stateful en stateless, tenemos que deshabilitar esos formularios de loging
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //indicamos que URls estan disponibles y cuales no
                .authorizeHttpRequests(req -> {
                    //va a permitir hacer un request que se haga con un Login con un post
                    req.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    //tabien se le dice que bloquea el resto de URLs
                    req.anyRequest().authenticated(); //quiere decir que tiene que estar autenticado para el resto de opciones

                })
                //indica que ejecute primero nuestro filtro y le luego le indicamos el siguiente
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) //UsernamePasswordAuthenticationFilter es el filtro de Spring Boot

                // 👇 CONFIGURACIÓN PARA DEVOLVER 401 EN LUGAR DE 403 CUANDO EL TOKEN ES INVÁLIDO
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    "Token inválido o expirado. Por favor, inicia sesión nuevamente."
                            );
                        })
                )

                .build();
    }

    //vamos a crear una clase que devuelva un authenticationManager

    @Bean // se usa para que este disponible ese metodo quien lo pida lo va a poder encontrar
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //se crea un metodo ara sprint security sepa que existe un metodo que devuelve el tipo de Hashing que se utiliza
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}