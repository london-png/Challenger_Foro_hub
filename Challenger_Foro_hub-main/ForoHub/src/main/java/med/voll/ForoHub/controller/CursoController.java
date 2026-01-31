// med.voll.ForoHub.controller.CursoController.java
package med.voll.ForoHub.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosRegistroCurso;
import med.voll.ForoHub.domain.Curso;
import med.voll.ForoHub.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    public ResponseEntity<Curso> crearCurso(
            @RequestBody @Valid DatosRegistroCurso datos,
            UriComponentsBuilder uriBuilder) {

        // Verificar si ya existe un curso con ese nombre
        if (cursoRepository.existsByNombre(datos.nombre())) {
            throw new CursoDuplicadoException("Ya existe un curso con ese nombre.");
        }

        // Crear la entidad a partir del DTO
        Curso curso = new Curso();
        curso.setNombre(datos.nombre());
        curso.setCategoria(datos.categoria());

        // Guardar en base de datos
        var saved = cursoRepository.save(curso);

        // Construir URI de recurso creado
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(uri).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoRepository.findAll());
    }

    // ✅ Manejador de excepciones personalizado
    @ExceptionHandler(CursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleCursoDuplicado(CursoDuplicadoException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                "/cursos"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ✅ Clase de excepción personalizada
    public static class CursoDuplicadoException extends RuntimeException {
        public CursoDuplicadoException(String message) {
            super(message);
        }
    }

    // ✅ Clase de respuesta de error
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
    }
}