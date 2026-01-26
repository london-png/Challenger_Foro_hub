// med.voll.ForoHub.controller.TopicoController.java
package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.*;
import med.voll.ForoHub.filtro.FiltroDatosTopico;
import med.voll.ForoHub.mapper.TopicoMapper;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.TopicoRepository;
import med.voll.ForoHub.service.TopicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controlador REST para gestionar operaciones CRUD sobre Tópicos.
 *
 * Responsabilidades:
 * - Manejar solicitudes HTTP (GET, POST, PUT, DELETE)
 * - Validar parámetros de entrada
 * - Coordinar con el servicio de negocio (TopicoService)
 * - Mapear entidades a DTOs para las respuestas
 * - Gestionar códigos de estado HTTP adecuados
 */
@RestController
@RequestMapping("/topicos")
public class TopicoController {

    // Servicio de negocio para lógica de tópicos
    private final TopicoService topicoService;

    // Mapper para convertir entidades a DTOs
    private final TopicoMapper topicoMapper;

    // Repositorio para operaciones directas (usado en endpoints simples)
    private final TopicoRepository topicoRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param topicoService Servicio de negocio para tópicos
     * @param topicoMapper Mapper para conversiones entidad-DTO
     * @param topicoRepository Repositorio para acceso a datos
     */
    public TopicoController(TopicoService topicoService, TopicoMapper topicoMapper, TopicoRepository topicoRepository) {
        this.topicoService = topicoService;
        this.topicoMapper = topicoMapper;
        this.topicoRepository = topicoRepository;
    }

    /**
     * Registra un nuevo tópico en el sistema.
     *
     * Método: POST /topicos
     *
     * @param datos Datos de registro del tópico (validados con @Valid)
     * @param uriBuilder Utilidad para construir URIs de recursos creados
     * @return ResponseEntity con código 201 Created y encabezado Location
     */
    @PostMapping
    public ResponseEntity<DatosDetalleTopico> registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriBuilder) {
        // Delegar la creación del tópico al servicio de negocio
        Topico topico = topicoService.registrar(datos);

        // Guardar el tópico en la base de datos
        Topico savedTopico = topicoRepository.save(topico);

        // Construir la URI del recurso recién creado para el encabezado Location
        var location = uriBuilder.path("/topicos/{id}").buildAndExpand(savedTopico.getId()).toUri();

        // Convertir la entidad a DTO y devolver la respuesta
        return ResponseEntity.created(location).body(topicoMapper.toDetalleDto(savedTopico));
    }

    /**
     * Lista todos los tópicos con opción de filtrar por curso y año.
     *
     * Método: GET /topicos?nombreCurso=...&ano=...
     *
     * @param nombreCurso Nombre del curso para filtrar (opcional)
     * @param ano Año para filtrar (opcional)
     * @param paginacion Configuración de paginación (tamaño, orden, etc.)
     * @return Página de DTOs DatosListaTopico
     */
    @GetMapping
    public Page<DatosListaTopico> listar(
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = {"fechaCreacion"}, direction = Sort.Direction.DESC) Pageable paginacion) {

        // Realizar la búsqueda directamente en el repositorio (operación simple)
        return topicoRepository.findByCursoNombreAndAno(nombreCurso, ano, paginacion)
                .map(topicoMapper::toListaDto); // Convertir cada entidad a DTO
    }

    /**
     * Busca tópicos por nombre de curso y año con validación completa.
     *
     * Método: POST /topicos/buscar
     *
     * @param filtros Filtros de búsqueda (nombreCurso y ano, ambos obligatorios)
     * @param paginacion Configuración de paginación
     * @return Página de DTOs DatosListaTopico o error 404 si no hay resultados
     */
    @PostMapping("/buscar")
    public ResponseEntity<Page<DatosListaTopico>> buscarPorFiltros(
            @RequestBody @Valid FiltroDatosTopico filtros,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // Delegar la búsqueda al servicio de negocio (incluye validación compleja)
        Page<Topico> topicos = topicoService.buscarPorFiltros(filtros, paginacion);

        // Convertir la página de entidades a página de DTOs
        return ResponseEntity.ok(topicos.map(topicoMapper::toListaDto));
    }

    /**
     * Obtiene los detalles de un tópico específico.
     *
     * Método: GET /topicos/{id}
     *
     * @param id ID del tópico a consultar
     * @return DTO DatosDetalleTopico con información completa del tópico
     */
    @GetMapping("/{id}")
    public ResponseEntity<DatosDetalleTopico> detalle(@PathVariable Long id) {
        // Validación básica del ID
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del tópico es obligatorio y debe ser válido.");
        }

        // Buscar el tópico en la base de datos
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        // Convertir a DTO y devolver la respuesta
        return ResponseEntity.ok(topicoMapper.toDetalleDto(topico));
    }

    /**
     * Actualiza un tópico existente.
     *
     * Método: PUT /topicos
     *
     * @param datos Datos de actualización del tópico (validados con @Valid)
     * @return DTO DatosDetalleTopico con la información actualizada
     */
    @PutMapping
    public ResponseEntity<DatosDetalleTopico> actualizar(@RequestBody @Valid DatosActualizacionTopico datos) {
        // Validación básica del ID
        if (datos.id() == null || datos.id() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'id' es obligatorio y debe ser positivo.");
        }

        // Delegar la actualización al servicio de negocio
        Topico topico = topicoService.actualizar(datos);

        // Guardar los cambios en la base de datos
        Topico savedTopico = topicoRepository.save(topico);

        // Convertir a DTO y devolver la respuesta
        return ResponseEntity.ok(topicoMapper.toDetalleDto(savedTopico));
    }

    /**
     * Elimina lógicamente un tópico (marca como inactivo).
     *
     * Método: DELETE /topicos/{id}
     *
     * @param id ID del tópico a eliminar
     * @return ResponseEntity con código 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Validación básica del ID
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del tópico es obligatorio.");
        }

        // Delegar la eliminación al servicio de negocio
        topicoService.eliminar(id);

        // Devolver respuesta sin contenido
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos los tópicos que tienen al menos una respuesta marcada como solución.
     *
     * Este endpoint devuelve una lista de tópicos resueltos, pero solo incluye
     * la información básica del tópico (sin la solución asociada).
     *
     * Método: GET /topicos/con-solucion
     *
     * @param paginacion Configuración de paginación
     * @return Página de DTOs DatosListaTopico con tópicos resueltos
     */
    @GetMapping("/con-solucion")
    public ResponseEntity<Page<DatosListaTopico>> listarTopicosConSolucion(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // Realizar la búsqueda directamente en el repositorio
        Page<Topico> topicos = topicoRepository.findAllWithSolucion(paginacion);

        // Convertir la página de entidades a página de DTOs
        return ResponseEntity.ok(topicos.map(topicoMapper::toListaDto));
    }

    /**
     * Lista todos los tópicos con su solución asociada.
     *
     * Este endpoint devuelve una lista de tópicos resueltos incluyendo
     * tanto la información del tópico como la de su solución asociada.
     *
     * Método: GET /topicos/con-solucion-detallada
     *
     * @param paginacion Configuración de paginación
     * @return Página de DTOs DatosTopicoConSolucion con tópicos y sus soluciones
     */
    @GetMapping("/con-solucion-detallada")
    public ResponseEntity<Page<DatosTopicoConSolucion>> listarTopicosConSolucionDetallada(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // Delegar la obtención de tópicos con solución al servicio de negocio
        Page<DatosTopicoConSolucion> topicos = topicoService.listarTopicosConSolucion(paginacion);

        // Devolver la respuesta con el DTO especial que incluye la solución
        return ResponseEntity.ok(topicos);
    }
}