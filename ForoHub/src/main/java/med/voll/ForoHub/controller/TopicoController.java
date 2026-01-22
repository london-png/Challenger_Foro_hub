package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosActualizacionTopico;
import med.voll.ForoHub.datos.DatosDetalleTopico;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import med.voll.ForoHub.filtro.FiltroDatosTopico;
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
    public ResponseEntity<Long> registrar(@RequestBody @Valid DatosRegistroTopico datos) {

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

        Topico savedTopico = topicoRepository.save(topico);//guarda y optiene la entidad persistida

        //llamamos al metodo  para poder guardar en la base de datos
        //topicoRepository.save(topico);

        //responde con 201
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopico.getId());

    }

    //metodo para listar los topicos que se han creado y ordenarlos por fecha en orden descendente
    @GetMapping // recibimos de los que son los verbos  http
    @Transactional(readOnly = true)
    public Page<DatosListaTopico> listar(
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = {"fechaCreacion"},direction = Sort.Direction.DESC) Pageable paginacion) {
        return topicoRepository.findByCursoNombreAndAno(nombreCurso, ano, paginacion)
                .map(DatosListaTopico::new);
    }

    //busquedad por Json(post)
    @PostMapping("/buscar")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> buscarPorFiltros(
            @RequestBody @Valid FiltroDatosTopico filtros,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // Validación manual (opcional, pero clara)
        if (filtros.nombreCurso() == null || filtros.nombreCurso().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'nombreCurso' es obligatorio.");
        }
        if (filtros.ano() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'ano' es obligatorio.");
        }

        // Realizar la búsqueda
        Page<Topico> topicos = topicoRepository.findByCursoNombreAndAno(
                filtros.nombreCurso().trim(),
                filtros.ano(),
                paginacion
        );

        // Si no hay resultados, verificar si el curso existe en la base de datos
        if (topicos.isEmpty()) {
            boolean cursoExiste = cursoRepository.existsByNombre(filtros.nombreCurso().trim());
            if (!cursoExiste) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nombre del curso no existe.");
            }
            // Si el curso existe pero no hay tópicos, simplemente devuelve página vacía (opcional)
        }

        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }
    //metodo para optener el topico por medio del id
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<DatosDetalleTopico> detalle(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del tópico es obligatorio y debe ser válido.");
        }

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        DatosDetalleTopico datos = new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso().getNombre()
        );
        return ResponseEntity.ok(datos);
    }
    //creamos el metodo para actualizar los topicos (PUT)

    @PutMapping
    @Transactional
    public ResponseEntity<DatosDetalleTopico> actualizar(
            @RequestBody @Valid DatosActualizacionTopico datos) {

        // Validar que el ID sea válido
        if (datos.id() == null || datos.id() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'id' es obligatorio y debe ser positivo.");
        }

        // Buscar el tópico existente
        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        // Actualizar solo los campos que no sean null
        if (datos.titulo() != null) {
            topico.setTitulo(datos.titulo());
        }
        if (datos.mensaje() != null) {
            topico.setMensaje(datos.mensaje());
        }
        if (datos.fechaCreacion() != null) {
            topico.setFechaCreacion(datos.fechaCreacion());
        }
        if (datos.status() != null) {
            topico.setStatus(datos.status());
        }
        if (datos.autor() != null) {
            topico.setAutor(datos.autor());
        }

        // Actualizar curso si se proporciona cursoId
        if (datos.cursoId() != null) {
            Curso nuevoCurso = cursoRepository.findById(datos.cursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado."));
            topico.setCurso(nuevoCurso);
        }

        // Guardar cambios
        topicoRepository.save(topico);

        // Devolver respuesta con los datos actualizados
        DatosDetalleTopico respuesta = new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso().getNombre()
        );

        return ResponseEntity.ok(respuesta);
    }


}
