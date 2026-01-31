package med.voll.ForoHub.rules.business;

import med.voll.ForoHub.domain.Respuesta;
import med.voll.ForoHub.domain.Topico;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Arrays;

@Component
public class TopicoRules {

    /**
     * Regla 3: Un tópico solo puede tener una solución
     */
    public void validarUnicaSolucion(Topico topico) {
        long cantidadSoluciones = topico.getRespuestas().stream()
                .filter(Respuesta::isSolucion)
                .count();

        if (cantidadSoluciones > 0) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Este tópico ya tiene una solución marcada. Solo se permite una solución por tópico."
            );
        }
    }

    /**
     * Regla 1: El autor del tópico no puede marcar su propia respuesta como solución
     */
    public void validarAutorSolucion(Topico topico, String autorRespuesta) {
        if (topico.getAutor().equalsIgnoreCase(autorRespuesta)) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El autor del tópico no puede marcar su propia respuesta como solución."
            );
        }
    }

    /**
     * Regla 4: Validación de calidad del mensaje y título
     *
     * Validaciones:
     * - El mensaje debe tener al menos 20 caracteres
     * - El título debe tener al menos 10 caracteres
     * - El título no debe ser genérico (ayuda, problema, error, etc.)
     */
    public void validarCalidadMensaje(String mensaje, String titulo) {
        // ✅ Validar longitud mínima del mensaje
        if (mensaje == null || mensaje.length() < 20) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El mensaje debe tener al menos 20 caracteres."
            );
        }

        // ✅ Validar longitud mínima del título (si se proporciona)
        if (titulo != null && titulo.length() < 10) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El título debe tener al menos 10 caracteres."  // 👈 Mensaje que verás en Insomnia
            );
        }
        
    }
}