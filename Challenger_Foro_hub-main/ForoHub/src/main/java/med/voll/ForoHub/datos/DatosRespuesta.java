// med.voll.ForoHub.datos.DatosRespuesta.java
package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DatosRespuesta(

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotBlank(message = "El autor es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El autor solo puede contener letras y espacios"
        )
        String autor,

        @NotNull(message = "El campo 'solucion' es obligatorio")
        String solucion
) {}