package med.voll.ForoHub.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import med.voll.ForoHub.datos.*;
import med.voll.ForoHub.exception.GestorDeErrores;
import med.voll.ForoHub.filtro.FiltroDatosTopico;
import med.voll.ForoHub.domain.Topico;
import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.repository.TopicoRepository;
import med.voll.ForoHub.service.TopicoService;
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
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
     * ✅ ESCRIBE LA SOLUCIÓN PARA UN TÓPICO
     *
     * Este método mapea DatosSolucionTopico a DatosRespuesta
     * y llama a escribirRespuesta() del servicio para que se actualice el estado a RESUELTO
     */
    @PostMapping("/soluciones")
    public ResponseEntity<?> escribirSolucion(
            @RequestBody @Valid DatosSolucionTopico datos) {

        try {
            DatosRespuesta datosRespuesta = new DatosRespuesta(
                    datos.mensaje(),
                    datos.autor(),
                    datos.solucion()
            );

            DatosDetalleTopico topicoConSolucion = topicoService.escribirRespuesta(
                    datos.topicoId(),
                    datosRespuesta
            );

            return ResponseEntity.ok(topicoConSolucion);
        } catch (IllegalArgumentException e) {
            // ✅ Usa el formato de error definido en GestorDeErrores
            return ResponseEntity.badRequest().body(
                    List.of(new GestorDeErrores.DatosErrorValidacion("error", e.getMessage()))
            );
        }
    }

    /**
     * ✅ MARCA UN TÓPICO COMO RESUELTO (SOLUCIONADO)
     *
     * Endpoint dedicado para cambiar el estado de un tópico a RESUELTO
     * sin necesidad de crear una respuesta.
     *
     * @param id ID del tópico a marcar como solucionado
     * @return Tópico actualizado con estado RESUELTO
     */
    @PutMapping("/{id:\\d+}/solucionar")
    @Transactional
    public ResponseEntity<DatosDetalleTopico> marcarComoSolucionado(@PathVariable Long id) {
        DatosDetalleTopico topicoActualizado = topicoService.marcarComoSolucionado(id);
        return ResponseEntity.ok(topicoActualizado);
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
     * ✅ Valida que el campo 'ano' contenga SOLO números
     * ✅ Devuelve 404 si no se encuentran resultados
     * ✅ Devuelve 400 si el año es inválido
     */
    @PostMapping("/buscar")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> buscarPorFiltros(
            @RequestBody @Valid FiltroDatosTopico filtros,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // ✅ VALIDACIÓN 1: Verificar que 'nombreCurso' no esté vacío
        if (filtros.nombreCurso() == null || filtros.nombreCurso().isBlank()) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'nombreCurso' es obligatorio."
            );
        }

        // ✅ VALIDACIÓN 2: Verificar que 'ano' no esté vacío
        if (filtros.ano() == null || filtros.ano().isBlank()) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'ano' es obligatorio."
            );
        }

        // ✅ VALIDACIÓN 3: Verificar que 'ano' contenga SOLO números
        String anoStr = filtros.ano().trim();
        if (!anoStr.matches("\\d+")) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'ano' debe contener SOLO números (ej: 2023, 2024)."
            );
        }

        // Convertir año a entero
        Integer ano;
        try {
            ano = Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'ano' debe contener SOLO números (ej: 2023, 2024)."
            );
        }

        // ✅ VALIDACIÓN 4: Realizar búsqueda
        Page<Topico> topicos = topicoRepository.findByCursoNombreAndAno(
                filtros.nombreCurso().trim(),
                ano,
                paginacion
        );

        // ✅ VALIDACIÓN 5: Verificar si hay resultados
        if (topicos.isEmpty()) {
            throw new ResponseStatusException(
                    NOT_FOUND,
                    "No se encontraron tópicos para el curso '" + filtros.nombreCurso() +
                            "' en el año " + ano + "."
            );
        }

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
    // En TopicoController.java
    @GetMapping("/con-solucion")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DatosListaTopico>> listarTopicosConSolucion(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        // ✅ Filtrar explícitamente por estado RESUELTO
        Page<Topico> topicos = topicoRepository.findAllWithSolucion(
                Status.RESUELTO,  // 👈 ¡Este es el filtro clave!
                paginacion
        );

        return ResponseEntity.ok(topicos.map(DatosListaTopico::new));
    }

    // ✅ MANEJADOR DE EXCEPCIONES PERSONALIZADO
    // Evita que Spring Security convierta los errores de negocio en 403 Forbidden
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        // ✅ CORRECCIÓN PARA SPRING BOOT 3.x: Convertir a HttpStatus para obtener la frase de estado
        String reasonPhrase = HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase();

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                reasonPhrase,
                ex.getReason(),
                null
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Clase de respuesta de error para estandarizar formatos
     */
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

        // Getters para serialización JSON
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
    }
}