package med.voll.ForoHub.datos;

import java.time.LocalDateTime;

/**
 * DTO (Objeto de Transferencia de Datos) que representa los detalles completos de una respuesta en el foro.
 * Se utiliza en las respuestas HTTP para exponer únicamente la información relevante al cliente,
 * sin revelar detalles internos del modelo de dominio ni relaciones complejas.
 *
 * @param id             Identificador único de la respuesta.
 * @param mensaje        Contenido textual de la respuesta.
 * @param fechaCreacion  Fecha y hora en que se creó la respuesta (en formato ISO-8601 cuando se serializa a JSON).
 * @param autor          Nombre o identificador del usuario que publicó la respuesta.
 * @param solucion       Indicador booleano que señala si esta respuesta resuelve el tópico (true/false).
 * @param topicoId       Identificador del tópico al que pertenece esta respuesta.
 */
public record DatosDetalleRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String autor,
        Boolean solucion,
        Long topicoId
) {}