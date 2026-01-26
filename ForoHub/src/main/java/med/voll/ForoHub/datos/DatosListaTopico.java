package med.voll.ForoHub.datos;

import med.voll.ForoHub.model.Status;
import med.voll.ForoHub.model.Topico;

import java.time.LocalDateTime;

/**
 * DTO (Objeto de Transferencia de Datos) utilizado para representar un tópico en listados de la API.
 * Incluye información básica del tópico y datos relevantes del curso asociado (nombre y categoría),
 * evitando la exposición directa de entidades JPA y relaciones complejas.
 *
 * <p>Este DTO es ideal para endpoints que devuelven una colección de tópicos (por ejemplo, al listar todos los tópicos),
 * ya que proporciona contexto suficiente sin sobrecargar la respuesta con datos innecesarios.</p>
 *
 * @param id                Identificador único del tópico.
 * @param titulo            Título del tópico.
 * @param mensaje           Contenido o descripción inicial del tópico.
 * @param fechaCreacion     Fecha y hora en que se creó el tópico.
 * @param status            Estado actual del tópico (ej. ACTIVO, CERRADO), definido por el enum {@link Status}.
 * @param autor             Nombre o identificador del usuario que creó el tópico.
 * @param cursoId           Identificador del curso asociado al tópico (puede ser null si no hay curso vinculado).
 * @param nombreCurso       Nombre del curso asociado (puede ser null).
 * @param categoriaCurso    Categoría del curso asociado (puede ser null).
 */
public record DatosListaTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        Long cursoId,
        String nombreCurso,
        String categoriaCurso
) {

    /**
     * Constructor de conveniencia que permite crear una instancia de {@link DatosListaTopico}
     * a partir de una entidad {@link Topico}.
     *
     * <p>Maneja de forma segura los casos en que el curso asociado al tópico sea {@code null},
     * evitando errores {@code NullPointerException}.</p>
     *
     * @param topico Entidad {@link Topico} de la cual se extraerán los datos para el DTO.
     */
    public DatosListaTopico(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso() != null ? topico.getCurso().getId() : null,
                topico.getCurso() != null ? topico.getCurso().getNombre() : null,
                topico.getCurso() != null ? topico.getCurso().getCategoria() : null
        );
    }
}