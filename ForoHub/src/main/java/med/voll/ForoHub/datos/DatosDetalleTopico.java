package med.voll.ForoHub.datos;

import med.voll.ForoHub.model.Status;
import java.time.LocalDateTime;

/**
 * DTO (Objeto de Transferencia de Datos) que representa los detalles completos de un tópico del foro.
 * Se utiliza en las respuestas de la API para proporcionar al cliente toda la información relevante
 * de un tópico, manteniendo una separación clara entre la capa de dominio y la capa de presentación.
 *
 * @param id              Identificador único del tópico.
 * @param titulo          Título del tópico (ej. "¿Cómo manejar excepciones en Spring Boot?").
 * @param mensaje         Contenido principal o descripción del tópico.
 * @param fechaCreacion   Fecha y hora en que se creó el tópico (serializada automáticamente a ISO-8601 en JSON).
 * @param status          Estado actual del tópico (por ejemplo: ACTIVO, CERRADO, etc.), definido por el enum {@link Status}.
 * @param autor           Nombre o identificador del usuario que creó el tópico.
 * @param nombreCurso     Nombre del curso asociado al tópico (ej. "Spring Boot Avanzado").
 */
public record DatosDetalleTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        String nombreCurso
) {}