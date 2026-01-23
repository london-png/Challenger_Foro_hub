package med.voll.ForoHub.datos;

import java.time.LocalDateTime;

public record DatosDetalleRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String autor,
        Boolean solucion,
        Long topicoId
) {}