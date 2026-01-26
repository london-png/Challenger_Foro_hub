package med.voll.ForoHub.datos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para recibir los datos necesarios al registrar un nuevo tópico en el foro.
 * Contiene únicamente los campos requeridos para la creación, sin incluir ID ni fecha (se generan automáticamente).
 *
 * <p>Todos los campos son obligatorios según las reglas de negocio del sistema.</p>
 */
public record DatosRegistroTopico(

        /**
         * Título del tópico. No puede estar vacío ni contener solo espacios.
         */
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        /**
         * Contenido o descripción inicial del tópico. No puede estar vacío.
         */
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        /**
         * Nombre o identificador del autor que crea el tópico. Requerido.
         */
        @NotBlank(message = "El autor es obligatorio")
        String autor,

        /**
         * Identificador del curso asociado al tópico.
         * Debe ser un número entero positivo (por ejemplo, 1, 2, 3...).
         *
         * <p>Se valida con {@link NotNull} para evitar valores nulos y con {@link Min}
         * para garantizar que sea al menos 1 (evitando IDs inválidos como 0 o negativos).</p>
         */
        @NotNull(message = "El cursoId es obligatorio")
        @Min(value = 1, message = "El cursoId debe ser un número entero positivo")
        Long cursoId  // Representa el ID del curso, no el objeto completo
) {}