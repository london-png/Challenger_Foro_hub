package med.voll.ForoHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Indica que esta clase es una aplicación Spring Boot.
// La anotación @SpringBootApplication es una combinación de:
// - @Configuration: permite registrar beans adicionales.
// - @EnableAutoConfiguration: habilita la configuración automática de Spring Boot según las dependencias en el classpath.
// - @ComponentScan: escanea componentes, configuraciones y servicios en el paquete actual y subpaquetes.
@SpringBootApplication
public class ForoHubApplication {

    // Método principal que inicia la aplicación Spring Boot.
    // SpringApplication.run() arranca el contexto de la aplicación, configura el servidor embebido (por ejemplo, Tomcat)
    // y prepara todos los beans necesarios para ejecutar la aplicación.
    public static void main(String[] args) {
        SpringApplication.run(ForoHubApplication.class, args);
    }

}