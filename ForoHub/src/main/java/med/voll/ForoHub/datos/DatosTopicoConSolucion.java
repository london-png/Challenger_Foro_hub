// med.voll.ForoHub.datos.DatosTopicoConSolucion.java
package med.voll.ForoHub.datos;

import java.time.LocalDateTime;

/**
 * Record que representa un tópico junto con su solución asociada.
 * Se utiliza para endpoints que devuelven información detallada de tópicos resueltos.
 */
public record DatosTopicoConSolucion(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        String status,
        String autor,
        Long cursoId,
        String nombreCurso,
        String categoriaCurso,

        // Campos de la solución (pueden ser null si no hay solución)
        Long solucionId,
        String solucionMensaje,
        LocalDateTime solucionFechaCreacion,
        String solucionAutor
) {}