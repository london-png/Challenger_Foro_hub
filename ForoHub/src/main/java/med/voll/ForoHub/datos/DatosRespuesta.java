package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRespuesta(
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotBlank(message = "El autor es obligatorio")
        String autor,

        @NotNull(message = "El campo 'solucion' es obligatorio")
        Boolean solucion
) {}