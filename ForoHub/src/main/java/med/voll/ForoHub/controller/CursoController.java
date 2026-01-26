// med.voll.ForoHub.controller.CursoController.java
package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosRegistroCurso;
import med.voll.ForoHub.model.Curso;
import med.voll.ForoHub.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones CRUD sobre Cursos.
 *
 * Responsabilidades:
 * - Manejar solicitudes HTTP (GET, POST)
 * - Validar datos de entrada mediante DTOs
 * - Coordinar con el repositorio para operaciones de base de datos
 * - Devolver respuestas HTTP adecuadas con códigos de estado y cuerpos
 */
@RestController
@RequestMapping("/cursos")
public class CursoController {

    // Repositorio para acceder a la base de datos de cursos
    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Registra un nuevo curso en el sistema.
     *
     * Método: POST /cursos
     *
     * @param datos Datos de registro del curso (validados con @Valid)
     * @param uriBuilder Utilidad para construir URIs de recursos creados
     * @return ResponseEntity con código 201 Created, encabezado Location y cuerpo del curso creado
     */
    @PostMapping
    public ResponseEntity<Curso> crearCurso(
            @RequestBody @Valid DatosRegistroCurso datos,
            UriComponentsBuilder uriBuilder) {

        // Verificar si ya existe un curso con ese nombre para evitar duplicados
        if (cursoRepository.existsByNombre(datos.nombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un curso con ese nombre.");
        }

        // Crear una nueva entidad Curso a partir del DTO de registro
        Curso curso = new Curso();
        curso.setNombre(datos.nombre());
        curso.setCategoria(datos.categoria());

        // Guardar el curso en la base de datos (el ID será generado automáticamente)
        var saved = cursoRepository.save(curso);

        // Construir la URI del recurso recién creado para el encabezado Location
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(saved.getId()).toUri();

        // ✅ DEVOLVER EL CUERPO DEL CURSO CREADO (antes usaba .build() sin cuerpo)
        return ResponseEntity.created(uri).body(saved);
    }

    /**
     * Lista todos los cursos existentes en el sistema.
     *
     * Método: GET /cursos
     *
     * @return ResponseEntity con código 200 OK y lista de cursos en el cuerpo
     */
    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        // Obtener todos los cursos de la base de datos
        return ResponseEntity.ok(cursoRepository.findAll());
    }
}