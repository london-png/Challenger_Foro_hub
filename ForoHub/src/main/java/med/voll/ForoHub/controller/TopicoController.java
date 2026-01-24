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

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private final TopicoRepository topicoRepository;
    private final CursoRepository cursoRepository;

    public TopicoController(TopicoRepository topicoRepository, CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Long> registrar(@RequestBody @Valid DatosRegistroTopico datos) {

        // === Validación personalizada de cursoId ===
        Long cursoId;
        try {
            cursoId = Long.parseLong(datos.cursoId().trim());
            if (cursoId <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero positivo.");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero válido.");
        }

        // Verificar duplicados
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un tópico con ese título y mensaje.");
        }

        // Buscar el curso real por ID
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        // Crear el tópico
        Topico topico = new Topico(
                null,
                datos.titulo(),
                datos.mensaje(),
                LocalDateTime.now(),
                Status.ABIERTO,
                datos.autor(),
                curso,
                new ArrayList<>()
        );

        Topico savedTopico = topicoRepository.save(topico);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopico.getId());
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Page<DatosListaTopico> listar(
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = {"fechaCreacion"}, direction = Sort.Direction.DESC) Pageable paginacion) {
        return topicoRepository.findByCursoNombreAndAno(nombreCurso, ano, paginacion)
                .map(DatosListaTopico::new);
    }

    @PostMapping("/buscar")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> buscarPorFiltros(
            @RequestBody @Valid FiltroDatosTopico filtros,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        if (filtros.nombreCurso() == null || filtros.nombreCurso().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'nombreCurso' es obligatorio.");
        }
        if (filtros.ano() == null || filtros.ano().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'ano' es obligatorio.");
        }

        String nombreCursoBuscado = filtros.nombreCurso().trim();
        String anoStr = filtros.ano().trim();

        // === Validar que 'ano' sea un número entero válido ===
        Integer ano;
        try {
            ano = Integer.parseInt(anoStr);
            if (ano < 1900 || ano > 2100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año debe estar entre 1900 y 2100.");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'ano' debe ser un número entero válido.");
        }

        // === Normalizar para ignorar tildes y mayúsculas ===
        String nombreNormalizado = normalizarTexto(nombreCursoBuscado);

        // Realizar la búsqueda
        Page<Topico> topicos = topicoRepository.findByCursoNombreAndAno(
                nombreCursoBuscado,
                ano,
                paginacion
        );

        // Si no hay resultados, verificar si existe algún curso cuyo nombre normalizado coincida
        if (topicos.isEmpty()) {
            boolean cursoExiste = false;
            List<Curso> todosCursos = cursoRepository.findAll();

            for (Curso curso : todosCursos) {
                if (normalizarTexto(curso.getNombre()).equals(nombreNormalizado)) {
                    cursoExiste = true;
                    break;
                }
            }

            if (!cursoExiste) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nombre del curso no existe.");
            }
            // Si el curso existe pero no hay tópicos, devuelve página vacía (sin error)
        }

        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }

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

    @PutMapping
    @Transactional
    public ResponseEntity<DatosDetalleTopico> actualizar(@RequestBody @Valid DatosActualizacionTopico datos) {

        if (datos.id() == null || datos.id() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'id' es obligatorio y debe ser positivo.");
        }

        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        if (datos.titulo() != null) topico.setTitulo(datos.titulo());
        if (datos.mensaje() != null) topico.setMensaje(datos.mensaje());
        if (datos.fechaCreacion() != null) topico.setFechaCreacion(datos.fechaCreacion());
        if (datos.status() != null) topico.setStatus(datos.status());
        if (datos.autor() != null) topico.setAutor(datos.autor());

        // === Validación personalizada de cursoId en actualización ===
        if (datos.cursoId() != null) {
            Long cursoId;
            try {
                cursoId = Long.parseLong(datos.cursoId().trim());
                if (cursoId <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero positivo.");
                }
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero válido.");
            }

            Curso nuevoCurso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado."));
            topico.setCurso(nuevoCurso);
        }

        topicoRepository.save(topico);

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

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del tópico es obligatorio.");
        }

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        topico.setActivo(false);
        topicoRepository.save(topico);

        return ResponseEntity.noContent().build();
    }

    /**
     * Normaliza un texto eliminando tildes y convirtiendo a minúsculas.
     * Ejemplo: "Configuración" → "configuracion"
     */
    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        return texto
                .toLowerCase()
                .replaceAll("[áéíóúÁÉÍÓÚ]", "aeiouAEIOU")
                .replaceAll("[àèìòùÀÈÌÒÙ]", "aeiouAEIOU")
                .replaceAll("[äëïöüÄËÏÖÜ]", "aeiouAEIOU");
    }
    // para mostrartodos los topicos que tienen una respuesta de solucion
    @GetMapping("/con-solucion")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> listarTopicosConSolucion(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {
        Page<Topico> topicos = topicoRepository.findAllWithSolucion(paginacion);
        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }
}