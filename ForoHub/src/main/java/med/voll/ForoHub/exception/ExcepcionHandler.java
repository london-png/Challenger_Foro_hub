// med.voll.ForoHub.exception.ExcepcionHandler.java
package med.voll.ForoHub.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//manejador de excepciones capturar errores específicos de la base de datos
@RestControllerAdvice
public class ExcepcionHandler {

    //el método manejarDuplicado se ejecutará siempre que ocurra una excepción del tipo DataIntegrityViolationException en cualquier controlador
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> manejarDuplicado(DataIntegrityViolationException e) {

        //validar los duplicados titulo y mensaje
        if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("titulo_mensaje")) {
            return ResponseEntity.badRequest().body("Ya existe un tópico con ese título y mensaje.");
        }
        return ResponseEntity.badRequest().body("Error de integridad en la base de datos.");
    }
}