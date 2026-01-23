// med.voll.ForoHub.controller.CursoController.java
package med.voll.ForoHub.controller;

import med.voll.ForoHub.model.Curso;
import med.voll.ForoHub.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    //controlador REST-- Indica que este método responde a una solicitud HTTP de tipo POST.
    @PostMapping
    public ResponseEntity<Void> crearCurso(@RequestBody Curso curso, UriComponentsBuilder uriBuilder) {
        var saved = cursoRepository.save(curso);
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).build();//permite construir respuestas HTTP completas (código de estado, encabezados, cuerpo).
    }
    //Endpoint para listar todos los cursos
    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoRepository.findAll());
    }
}
