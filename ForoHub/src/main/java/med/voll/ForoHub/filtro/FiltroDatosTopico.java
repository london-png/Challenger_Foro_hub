// med.voll.ForoHub.filtro.FiltroDatosTopico.java
package med.voll.ForoHub.filtro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record FiltroDatosTopico(

        @NotBlank(message = "El campo 'nombreCurso' es obligatorio.")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El nombre del curso solo puede contener letras y espacios."
        )
        String nombreCurso,

        @NotNull(message = "El campo 'ano' es obligatorio.")
        String ano  // ← Debe ser String para aceptar cualquier entrada y validar manualmente
) {}