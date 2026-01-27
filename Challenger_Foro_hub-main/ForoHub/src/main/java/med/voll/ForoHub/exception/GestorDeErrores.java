package med.voll.ForoHub.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Clase controladora global de excepciones para manejar errores comunes en la API REST.
 * Esta clase intercepta excepciones específicas y devuelve respuestas HTTP adecuadas.
 */
@RestControllerAdvice
public class GestorDeErrores {

    /**
     * Maneja las excepciones de tipo EntityNotFoundException (por ejemplo, cuando un recurso no se encuentra en la base de datos).
     * Devuelve una respuesta HTTP 404 Not Found sin cuerpo.
     *
     * @return ResponseEntity con estado 404.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity gestionarError404() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Maneja las excepciones lanzadas cuando falla la validación de los argumentos de un método controlador
     * (por ejemplo, cuando un campo anotado con @NotBlank es nulo o vacío).
     * Devuelve una respuesta HTTP 400 Bad Request con una lista de errores de validación.
     *
     * @param ex Excepción que contiene los detalles de los campos inválidos.
     * @return ResponseEntity con estado 400 y el cuerpo contiene una lista de objetos DatosErrorValidacion.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity gestionarError400(MethodArgumentNotValidException ex) {
        var errores = ex.getBindingResult().getFieldErrors();
        return ResponseEntity.badRequest().body(errores.stream().map(DatosErrorValidacion::new).toList());
    }

    /**
     * Record utilizado para representar de forma clara y concisa un error de validación.
     * Contiene el nombre del campo y el mensaje de error asociado.
     */
    public record DatosErrorValidacion(String campo, String mensaje) {
        /**
         * Constructor que recibe un FieldError de Spring y extrae el nombre del campo
         * y el mensaje de error predeterminado para construir una instancia de DatosErrorValidacion.
         *
         * @param error El objeto FieldError que contiene la información del error de validación.
         */
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}