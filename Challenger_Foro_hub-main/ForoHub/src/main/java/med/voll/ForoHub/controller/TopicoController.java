package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.*;
import med.voll.ForoHub.filtro.FiltroDatosTopico;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.TopicoRepository;
import med.voll.ForoHub.service.TopicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Controlador REST para gestionar operaciones CRUD sobre los Tópicos del foro.
 *
 * ✅ Responsabilidad: Solo recibir peticiones, delegar lógica a servicios y devolver respuestas.
 * ❌ No contiene lógica de negocio, validaciones complejas ni construcción manual de entidades.
 *
 * Usa {@link TopicoService} para operaciones de negocio y {@link TopicoRepository} solo para consultas
 * que no requieren lógica adicional (como búsquedas paginadas).
 */
@RestController
@RequestMapping("/topicos")
public class TopicoController {

    // === Inyección de dependencias ===
    private final TopicoService topicoService;
    private final TopicoRepository topicoRepository;

    public TopicoController(TopicoService topicoService, TopicoRepository topicoRepository) {
        this.topicoService = topicoService;
        this.topicoRepository = topicoRepository;
    }

    /**
     * Registra un nuevo tópico.
     *
     * - Valida campos obligatorios mediante @Valid + anotaciones en {@link DatosRegistroTopico}.
     * - Delega toda la lógica de creación al servicio.
     * - Devuelve 201 Created con el cuerpo completo del tópico creado.
     */
    @PostMapping
    public ResponseEntity<DatosDetalleTopico> registrar(@RequestBody @Valid DatosRegistroTopico datos) {
        DatosDetalleTopico topicoCreado = topicoService.registrar(datos);
        return ResponseEntity.status(CREATED).body(topicoCreado);
    }

    /**
     * Lista todos los tópicos con paginación y filtros opcionales por nombre de curso y año.
     *
     * - Usa el repositorio directamente porque es una consulta simple.
     * - Mapea automáticamente a {@link DatosListaTopico}.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public Page<DatosListaTopico> listar(
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = {"fechaCreacion"}, direction = Sort.Direction.DESC) Pageable paginacion) {
        return topicoRepository.findByCursoNombreAndAno(nombreCurso, ano, paginacion)
                .map(DatosListaTopico::new);
    }

    /**
     * Busca tópicos mediante un cuerpo JSON con filtros (nombreCurso y año).
     *
     * - Valida que los campos obligatorios estén presentes.
     * - Realiza conversión y búsqueda directa en el repositorio.
     * - Esta lógica podría moverse a un servicio si crece en complejidad.
     */
    @PostMapping("/buscar")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> buscarPorFiltros(
            @RequestBody @Valid FiltroDatosTopico filtros,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // Validaciones manuales (podrían reforzarse con @NotBlank en el DTO)
        if (filtros.nombreCurso() == null || filtros.nombreCurso().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El campo 'nombreCurso' es obligatorio.");
        }
        if (filtros.ano() == null || filtros.ano().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El campo 'ano' es obligatorio.");
        }

        // Convertir año a entero (asumimos que ya fue validado por @Valid en el DTO)
        Integer ano = Integer.valueOf(filtros.ano().trim());

        // Realizar búsqueda
        Page<Topico> topicos = topicoRepository.findByCursoNombreAndAno(
                filtros.nombreCurso().trim(),
                ano,
                paginacion
        );

        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }

    /**
     * Obtiene los detalles de un tópico por su ID.
     *
     * ⚠️ Este método NO incluye la solución asociada.
     * Si necesitas ver la solución, usa el endpoint "/topicos/soluciones/{id}".
     */
    @GetMapping("/{id:\\d+}") // 👈 Solo acepta IDs numéricos para evitar colisiones con rutas como "/con-solucion"
    public ResponseEntity<DatosDetalleTopico> detalle(@PathVariable Long id) {
        DatosDetalleTopico topico = topicoService.obtenerPorId(id);
        return ResponseEntity.ok(topico);
    }

    /**
     * Obtiene los detalles de un tópico por su ID, incluyendo su solución si existe.
     *
     * ✅ Este es el endpoint que debes usar cuando quieras ver un tópico solucionado
     * con toda su información (igual que en "/topicos/con-solucion").
     */
    @GetMapping("/soluciones/{id:\\d+}") // 👈 Solo acepta IDs numéricos
    @Transactional(readOnly = true)
    public ResponseEntity<DatosDetalleTopico> obtenerTopicoConSolucion(@PathVariable Long id) {
        DatosDetalleTopico topico = topicoService.obtenerPorIdConSolucion(id);
        return ResponseEntity.ok(topico);
    }

    /**
     * Actualiza un tópico existente (actualización parcial).
     *
     * - Valida campos mediante @Valid.
     * - Delega toda la lógica al servicio.
     */
    @PutMapping
    public ResponseEntity<DatosDetalleTopico> actualizar(@RequestBody @Valid DatosActualizacionTopico datos) {
        DatosDetalleTopico topicoActualizado = topicoService.actualizar(datos);
        return ResponseEntity.ok(topicoActualizado);
    }

    /**
     * Elimina lógicamente un tópico (soft delete).
     *
     * - Delega la operación al servicio.
     * - Devuelve 204 No Content.
     */
    @DeleteMapping("/{id:\\d+}") // 👈 Solo acepta IDs numéricos
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        topicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista tópicos que tienen al menos una respuesta marcada como "solución".
     *
     * - Consulta directa al repositorio.
     * - Mapeo automático a DTO.
     */
    @GetMapping("/con-solucion")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> listarTopicosConSolucion(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {
        Page<Topico> topicos = topicoRepository.findAllWithSolucion(paginacion);
        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }
}