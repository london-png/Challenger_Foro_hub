// med.voll.ForoHub.datos.DatosRegistroCurso.java
package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO utilizado para recibir los datos necesarios al registrar un nuevo curso en el sistema.
 * Este registro no incluye el ID, ya que se genera automáticamente al persistir la entidad.
 *
 * <p>Aplica validaciones estrictas para garantizar la integridad de los datos:
 * - El nombre y la categoría son obligatorios.
 * - Solo se permiten letras (incluyendo tildes y ñ del español) y espacios.</p>
 */
public record DatosRegistroCurso(

        /**
         * Nombre del curso (por ejemplo: "Fundamentos de Spring Boot").
         * No puede estar vacío y debe contener únicamente letras y espacios.
         */
        @NotBlank(message = "El nombre es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El nombre solo puede contener letras y espacios"
        )
        String nombre,

        /**
         * Categoría del curso (por ejemplo: "Desarrollo Backend").
         * No puede estar vacía y debe contener únicamente letras y espacios.
         */
        @NotBlank(message = "La categoría es obligatoria")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "La categoría solo puede contener letras y espacios"
        )
        String categoria
) {}