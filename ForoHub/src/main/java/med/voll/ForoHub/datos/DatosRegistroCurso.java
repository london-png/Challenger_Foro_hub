// med.voll.ForoHub.datos.DatosRegistroCurso.java
package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DatosRegistroCurso(

        @NotBlank(message = "El nombre es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El nombre solo puede contener letras y espacios"
        )
        String nombre,

        @NotBlank(message = "La categoría es obligatoria")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "La categoría solo puede contener letras y espacios"
        )
        String categoria
) {}