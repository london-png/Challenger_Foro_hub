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

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    public ResponseEntity<Void> crearCurso(
            @RequestBody @Valid DatosRegistroCurso datos,
            UriComponentsBuilder uriBuilder) {

        // Verificar si ya existe un curso con ese nombre
        if (cursoRepository.existsByNombre(datos.nombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un curso con ese nombre.");
        }

        // Crear la entidad a partir del DTO
        Curso curso = new Curso();
        curso.setNombre(datos.nombre());
        curso.setCategoria(datos.categoria());

        // Guardar en base de datos
        var saved = cursoRepository.save(curso);

        // Construir URI de recurso creado
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoRepository.findAll());
    }
}