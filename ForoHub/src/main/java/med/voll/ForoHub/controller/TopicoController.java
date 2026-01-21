package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import med.voll.ForoHub.model.Curso;
import med.voll.ForoHub.datos.DatosListaTopico;
import med.voll.ForoHub.model.Status;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.CursoRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController //un componente que maneja las solicitudes HTTP entrantes y devuelve respuestas, típicamente en formato JSON o XML.
@RequestMapping("/topicos") //para que Spring sepa que es un controller y va a atender topicos
public class TopicoController {

    //creamos nuestra insatancia del repository
    private TopicoRepository topicoRepository;
    private CursoRepository cursoRepository;

    public TopicoController(TopicoRepository topicoRepository, CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.cursoRepository = cursoRepository;
    }


    @Transactional // se usa cuando vamos a modificar algo en la base de datos
    @PostMapping //se utiliza para mapear métodos de un controlador a solicitudes HTTP POST.
    // creamos el metodo para topico
    public ResponseEntity<Void> registrar(@RequestBody @Valid DatosRegistroTopico datos) {

        //Verificar dupliados
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un tópico con ese título y mensaje.");
        }

        // Buscar el curso real por ID
        Curso curso = cursoRepository.findById(datos.cursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        // Crear el tópico con la entidad Curso
        Topico topico = new Topico(
                null,                      // id → JPA lo genera
                datos.titulo(),
                datos.mensaje(),
                LocalDateTime.now(),       // fechaCreacion
                Status.ABIERTO,           // status
                datos.autor(),
                curso,
                new ArrayList<>()         // respuestas
        );;// ya es una  entidad JPA

        //llamamos al metodo  para poder guardar en la base de datos
        topicoRepository.save(topico);

        //responde con 201
        return ResponseEntity.status(HttpStatus.CREATED).build();


        //System.out.println(datos); // es de tipo DTO
        //topicoRepository.save(new Topico(datos));
    }

    //metodo para listar los topicos que se han creado y ordenarlos por fecha en orden descendente
    @GetMapping// recibimos de los que son los verbos  http
    @Transactional(readOnly = true)
    public Page<DatosListaTopico> listar(@PageableDefault(size = 10, sort = {"fechaCreacion"},direction = Sort.Direction.DESC) Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(DatosListaTopico :: new);

    }


}
