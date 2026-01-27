/*package med.voll.ForoHub.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Spring Security es una aplicacion de Spring Boot

//Indica que esta clase es una fuente de configuración de beans
@Configuration

//habilita la seguridad web en la aplicación, integrando Spring Security con Spring MVC, proteje los endpoints
@EnableWebSecurity

public class SecurityConfigurations {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Configura las reglas de autorización para las rutas (endpoints) de tu API.
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Permite acceso a todas las rutas sin autenticación
                )
                .csrf(csrf -> csrf.disable()); // Desactiva CSRF para APIs REST

        return http.build();
    }
}*/
