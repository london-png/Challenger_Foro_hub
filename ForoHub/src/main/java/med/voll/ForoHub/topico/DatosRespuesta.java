package med.voll.ForoHub.topico;

import java.time.LocalDateTime;

public record DatosRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String autor,
        Boolean solucion // o String si es texto, pero boolean es más lógico para "solución"

) {
}
