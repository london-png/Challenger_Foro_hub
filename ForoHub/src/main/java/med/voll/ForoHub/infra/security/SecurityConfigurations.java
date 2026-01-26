package med.voll.ForoHub.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración de Spring Security para la aplicación ForoHub.
 *
 * <p>Actualmente, permite acceso público a todos los endpoints (sin autenticación),
 * lo cual es adecuado para APIs abiertas o durante fases tempranas de desarrollo.</p>
 *
 * <strong>Nota:</strong> Si en el futuro se requiere autenticación (por ejemplo, con JWT),
 * se deberán agregar filtros personalizados, configurar rutas protegidas y deshabilitar
 * comportamientos por defecto como formLogin() o sessionManagement().
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    /**
     * Define la cadena de filtros de seguridad para las solicitudes HTTP.
     *
     * <p>En esta configuración:
     * - Todas las rutas están permitidas sin autenticación ({@code permitAll()}).
     * - La protección CSRF está desactivada, ya que las APIs REST stateless no la requieren.
     * - No se configuran mecanismos de login, logout ni gestión de sesiones (implícito).</p>
     *
     * @param http Objeto para configurar la seguridad HTTP.
     * @return Cadena de filtros de seguridad construida.
     * @throws Exception si ocurre un error durante la construcción.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configura autorización de solicitudes
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // ← Acceso público total (¡revisar antes de producción!)
                )
                // Desactiva CSRF: necesario para APIs REST que usan tokens (JWT) y no cookies
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}