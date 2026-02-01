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
     * Regla 1: Un tópico solo puede tener una solución
     */
    public void validarUnicaSolucion(Topico topico) {
        long cantidadSoluciones = topico.getRespuestas().stream()
                .filter(Respuesta::isSolucion)
                .count();

        if (cantidadSoluciones >= 1) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El tópico ID " + topico.getId() + " ya cuenta con una solución dada y está en estado RESUELTO. No se permite marcar múltiples soluciones para el mismo tópico."
            );
        }
    }

    /**
     * Regla 2: El autor del tópico no puede marcar su propia respuesta como solución
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
     * Regla 3: Validación de calidad del mensaje y título
     */
    public void validarCalidadMensaje(String mensaje, String titulo) {
        // Validar longitud mínima del mensaje
        if (mensaje == null || mensaje.length() < 20) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El mensaje debe tener al menos 20 caracteres."
            );
        }

        // Validar longitud mínima del título (si se proporciona)
        if (titulo != null && titulo.length() < 10) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El título debe tener al menos 10 caracteres."
            );
        }
    }
    /**
     * Valida que un tópico solucionado no permita más respuestas
     *
     * @param topico El tópico al que se quiere agregar una respuesta
     * @throws ResponseStatusException si el tópico ya está solucionado
     */
    public void validarTopicoNoSolucionado(Topico topico) {
        // Verificar si el tópico ya está en estado RESUELTO
        if (topico.getStatus() == med.voll.ForoHub.domain.Status.RESUELTO) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El tópico ID " + topico.getId() + " ya está solucionado y no puede recibir más respuestas. " +
                            "Solo se permite una solución por tópico."
            );
        }
    }

    /**
     * Verifica si un tópico ya tiene una respuesta marcada como solución
     *
     * @param topico El tópico a verificar
     * @return true si el tópico ya tiene una solución, false en caso contrario
     */
    public boolean tieneSolucion(Topico topico) {
        return topico.getRespuestas().stream()
                .anyMatch(respuesta -> respuesta.isSolucion());
    }

}