// med.voll.ForoHub.datos.DatosRegistroCurso.java
package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DatosRegistroCurso(

        //validacion para que el nombre no reciba numeros ni caracteres especiales
        @NotBlank(message = "El nombre es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El nombre solo puede contener letras y espacios"
        )
        String nombre,

        //validacion para que el message no reciba numeros ni caracteres especiales
        @NotBlank(message = "La categoría es obligatoria")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "La categoría solo puede contener letras y espacios"
        )
        String categoria
) {}