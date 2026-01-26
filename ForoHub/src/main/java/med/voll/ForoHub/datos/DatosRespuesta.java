// med.voll.ForoHub.datos.DatosRespuesta.java
package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO utilizado para recibir los datos de una nueva respuesta en un tópico del foro.
 * Este registro no incluye ID ni fecha de creación, ya que se generan automáticamente.
 *
 * <p>Todos los campos son obligatorios según las reglas de negocio.</p>
 */
public record DatosRespuesta(

        /**
         * Contenido textual de la respuesta. No puede estar vacío.
         */
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        /**
         * Nombre del autor que publica la respuesta.
         * Solo se permiten letras (incluyendo tildes y ñ del español) y espacios.
         */
        @NotBlank(message = "El autor es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ\\s]+$",
                message = "El autor solo puede contener letras y espacios"
        )
        String autor,

        /**
         * Indicador de si esta respuesta resuelve el tópico.
         * Debe ser {@code true} o {@code false}.
         *
         * <p>Al usar el tipo {@link Boolean}, Spring Boot (vía Jackson) valida automáticamente
         * que el valor JSON sea un booleano válido, rechazando cadenas como "sí", "1", etc.</p>
         */
        @NotNull(message = "El campo 'solucion' es obligatorio")
        Boolean solucion
) {}