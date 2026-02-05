package med.voll.ForoHub.datos;

import med.voll.ForoHub.domain.Respuesta;
import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.domain.Topico;
import java.time.LocalDateTime;

// DTO (Data Transfer Object) para representar un tópico en listas paginadas.
// Incluye:
// Datos básicos del tópico.
// Información del curso asociado.
// La solución (si existe), extraída de las respuestas marcadas como "esSolucion".
// Este DTO se usa en endpoints como:
// GET /topicos
// POST /topicos/buscar
// GET /topicos/con-solucion

public record DatosListaTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        Long cursoId,
        String nombreCurso,
        String categoriaCurso,
        String solucion  // la respuesta marcada como solución
) {

     // Constructor que recibe una entidad {@link Topico} y extrae todos sus datos,
     // incluyendo la solución si existe.
     // Importante: Asegúrate de que las respuestas del tópico estén cargadas
     // (usando JOIN FETCH o @EntityGraph en el repositorio), de lo contrario
     // {@code topico.getRespuestas()} podría estar vacío o lanzar LazyInitializationException.

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
                topico.getCurso() != null ? topico.getCurso().getCategoria() : null,
                obtenerSolucion(topico)  // Extrae la solución si existe
        );
    }

     // Método auxiliar privado que busca en la lista de respuestas del tópico
     // aquella marcada como "solución" y devuelve su contenido.
     //  @param topico El tópico del cual se quiere extraer la solución.
     // @return El contenido de la solución, o {@code "No Solucionado"} si no existe ninguna.

    private static String obtenerSolucion(Topico topico) {
        return topico.getRespuestas().stream()
                .filter(Respuesta::isSolucion)      // Filtra solo las respuestas con esSolucion = true
                .findFirst()                          // Toma la primera (debería haber solo una)
                .map(Respuesta::getMensaje)         // Extrae el contenido de la respuesta
                .orElse("No Solucionado");            // Si no hay solución, devuelve "No Solucionado"
    }
}