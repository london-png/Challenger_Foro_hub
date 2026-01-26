package med.voll.ForoHub.datos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import med.voll.ForoHub.datos.CursoValidationGroup;

/**
 * DTO (Data Transfer Object) utilizado para recibir los datos de actualización de un tópico.
 * <p>
 * Solo incluye los campos que el cliente está autorizado a modificar:
 * - título
 * - mensaje
 * - estado (status)
 * - curso
 * <p>
 * Campos como 'id', 'autor', 'fechaCreacion' o 'solucion' NO se incluyen,
 * ya que no deben ser modificables por el cliente por razones de seguridad e integridad del dominio.
 */
public record DatosActualizacionTopico(

        /**
         * Título del tópico. Campo obligatorio.
         * <p>
         * Permite letras (incluyendo acentos), números, espacios y signos de puntuación comunes.
         * No permite símbolos especiales potencialmente peligrosos (como <, >, &, etc.).
         */
        @NotBlank(message = "El título es obligatorio")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ0-9\\s.,;:'\"!?()\\[\\]{}]+$",
                message = "El título contiene caracteres no permitidos"
        )
        String titulo,

        /**
         * Contenido o mensaje del tópico. Campo obligatorio.
         * <p>
         * Se valida que no esté vacío ni compuesto solo por espacios en blanco.
         */
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        /**
         * Estado del tópico (por ejemplo: "ACTIVO", "CERRADO").
         * <p>
         * Este campo es opcional. Si no se proporciona, el valor actual del tópico se conserva.
         * Debe coincidir con uno de los valores del enum {@link med.voll.ForoHub.model.Status}.
         */
        String status,

        /**
         * Nombre del curso asociado al tópico.
         * <p>
         * Opcional en actualizaciones. Si se proporciona, debe contener únicamente
         * letras (con acentos), números y espacios.
         * <p>
         * La validación se aplica solo cuando se usa el grupo {@link CursoValidationGroup},
         * lo que permite validación condicional (por ejemplo, solo si el campo no es nulo).
         */
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ0-9\\s]+$",
                message = "El nombre del curso solo puede contener letras, números y espacios",
                groups = {CursoValidationGroup.class}
        )
        String curso

) {}