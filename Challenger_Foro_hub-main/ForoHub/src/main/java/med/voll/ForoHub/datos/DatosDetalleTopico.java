package med.voll.ForoHub.datos;

import med.voll.ForoHub.domain.Respuesta;
import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.domain.Topico;
import java.time.LocalDateTime;


// (Data Transfer Object) para representar los detalles completos de un tópico.
// Incluye:
// Datos básicos del tópico.
// Información del curso asociado.
// La solución (si existe), extraída de las respuestas marcadas como "solucion".
// Este DTO se usa en endpoints como:
// GET /topicos/{id}
//GET /topicos/soluciones/{id}
//PUT /topicos

public record DatosDetalleTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        String nombreCurso,
        String solucion  //marca la respuesta solucionada
) {

    // Constructor que recibe una entidad {@link Topico} y extrae todos sus datos,
    // incluyendo la solución si existe.
    // Importante: Asegúrate de que las respuestas del tópico estén cargadas
    // (usando JOIN FETCH o @EntityGraph en el repositorio), de lo contrario
    // {@code topico.getRespuestas()} podría estar vacío.

    public DatosDetalleTopico(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso() != null ? topico.getCurso().getNombre() : null,
                obtenerSolucion(topico)
        );
    }

     // Método auxiliar privado que busca en la lista de respuestas del tópico
     // aquella marcada como "solución" y devuelve su mensaje.
     // @param topico El tópico del cual se quiere extraer la solución.
     // @return El mensaje de la solución, o {@code null} si no existe ninguna.

    private static String obtenerSolucion(Topico topico) {
        return topico.getRespuestas().stream()
                .filter(Respuesta::isSolucion)      // Filtra solo las respuestas con solucion = true
                .findFirst()                          // Toma la primera (debería haber solo una)
                .map(Respuesta::getMensaje)           // Extrae el mensaje de la respuesta
                .orElse(null);                        // Si no hay solución, devuelve null
    }
}