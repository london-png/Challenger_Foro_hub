// med.voll.ForoHub.mapper.TopicoMapper.java
package med.voll.ForoHub.mapper;

import med.voll.ForoHub.datos.DatosDetalleTopico;
import med.voll.ForoHub.datos.DatosListaTopico;
import med.voll.ForoHub.model.Topico;
import org.springframework.stereotype.Component;

/**
 * Mapper (mapeador) encargado de convertir entidades Topico a DTOs (Data Transfer Objects).
 *
 * Responsabilidades:
 * - Convertir entidades Topico a DTOs para respuestas HTTP
 * - Mantener la separación entre capa de dominio (model) y capa de presentación (controller)
 * - Evitar exponer entidades directamente en la API REST
 */
@Component
public class TopicoMapper {

    /**
     * Convierte una entidad Topico a un DTO de detalle (DatosDetalleTopico).
     *
     * Este DTO se utiliza cuando se necesita mostrar información completa de un tópico,
     * incluyendo el nombre del curso asociado.
     *
     * @param topico Entidad Topico a convertir
     * @return DTO DatosDetalleTopico con la información formateada para la respuesta
     */
    public DatosDetalleTopico toDetalleDto(Topico topico) {
        return new DatosDetalleTopico(
                topico.getId(),                    // ID del tópico
                topico.getTitulo(),               // Título del tópico
                topico.getMensaje(),              // Mensaje del tópico
                topico.getFechaCreacion(),        // Fecha de creación
                topico.getStatus(),               // Estado del tópico (ABIERTO, CERRADO, etc.)
                topico.getAutor(),                // Autor del tópico
                topico.getCurso().getNombre()     // Nombre del curso asociado (no el objeto completo)
        );
    }

    /**
     * Convierte una entidad Topico a un DTO de lista (DatosListaTopico).
     *
     * Este DTO se utiliza cuando se muestran listas de tópicos (por ejemplo, en búsquedas paginadas).
     * Utiliza el constructor existente de DatosListaTopico que recibe una entidad Topico.
     *
     * @param topico Entidad Topico a convertir
     * @return DTO DatosListaTopico con la información resumida para listas
     */
    public DatosListaTopico toListaDto(Topico topico) {
        return new DatosListaTopico(topico);
    }
}