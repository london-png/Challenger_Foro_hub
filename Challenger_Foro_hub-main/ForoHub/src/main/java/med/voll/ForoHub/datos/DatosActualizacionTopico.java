package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotNull;
import med.voll.ForoHub.domain.Status;

import java.time.LocalDateTime;

public record DatosActualizacionTopico(
        @NotNull(message = "El ID es obligatorio")
        Long id,

        //estos campos son opcionales
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,

        //para actualizar el cuerso
          String cursoId
) {}