package med.voll.ForoHub.infra.security;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Controlador global de excepciones para manejar errores comunes en la API.
 * <p>
 * Esta clase centraliza el manejo de:
 * - Errores de validación de entrada (400 Bad Request)
 * - Recursos no encontrados (404 Not Found)
 * </p>
 *
 * <strong>Nota:</strong> Si ya tienes otro @RestControllerAdvice (como ExcepcionHandler),
 * considera fusionar ambos en una sola clase para evitar conflictos o inconsistencias.
 */
@RestControllerAdvice
public class GestorDeErrores {

    /**
     * Maneja excepciones cuando un recurso no se encuentra en la base de datos
     * (por ejemplo, al buscar un tópico o curso por ID inexistente).
     *
     * @return Respuesta HTTP 404 (Not Found) sin cuerpo.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> gestionarError404() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Maneja errores de validación en los DTOs anotados con @Valid.
     * Devuelve una lista estructurada de errores por campo.
     *
     * @param ex Excepción lanzada por Spring al fallar la validación.
     * @return Respuesta HTTP 400 con detalles de los campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> gestionarError400(MethodArgumentNotValidException ex) {
        List<DatosErrorValidacion> errores = ex.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    /**
     * Record interno para representar un error de validación de forma clara y serializable.
     * Se utiliza exclusivamente en respuestas de error 400.
     *
     * @param campo  Nombre del campo que falló la validación.
     * @param mensaje Mensaje descriptivo del error (definido en las anotaciones @NotBlank, @Pattern, etc.).
     */
    public record DatosErrorValidacion(String campo, String mensaje) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}