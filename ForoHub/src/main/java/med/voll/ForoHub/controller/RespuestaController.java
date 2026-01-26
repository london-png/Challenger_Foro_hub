package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosDetalleRespuesta;
import med.voll.ForoHub.datos.DatosRespuesta;
import med.voll.ForoHub.model.Respuesta;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.RespuestaRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar las respuestas asociadas a los tópicos del foro.
 * Proporciona endpoints para crear, listar y consultar respuestas, incluyendo aquellas marcadas como "solución".
 */
@RestController
@RequestMapping("/topicos")
public class RespuestaController {

    // Inyección de dependencias de los repositorios necesarios
    private final TopicoRepository topicoRepository;
    private final RespuestaRepository respuestaRepository;

    /**
     * Constructor que inyecta los repositorios de Tópico y Respuesta.
     *
     * @param topicoRepository Repositorio para operaciones sobre entidades Topico.
     * @param respuestaRepository Repositorio para operaciones sobre entidades Respuesta.
     */
    public RespuestaController(TopicoRepository topicoRepository, RespuestaRepository respuestaRepository) {
        this.topicoRepository = topicoRepository;
        this.respuestaRepository = respuestaRepository;
    }

    /**
     * Endpoint POST para agregar una nueva respuesta a un tópico específico.
     *
     * @param idTopico ID del tópico al que se asociará la respuesta.
     * @param datos Objeto DTO con los datos de la respuesta a crear (mensaje, autor, solucion).
     * @return ResponseEntity con los detalles de la respuesta creada y estado HTTP 201 (CREATED).
     * @throws ResponseStatusException si el tópico no existe, si 'solucion' no es 'true'/'false',
     *                                 o si ya existe una respuesta idéntica para ese tópico.
     */
    @PostMapping("/{idTopico}/respuestas")
    public ResponseEntity<DatosDetalleRespuesta> agregarRespuesta(
            @PathVariable Long idTopico,
            @RequestBody @Valid DatosRespuesta datos) {

        // Verificar que el tópico exista
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Validar que el campo 'solucion' sea estrictamente "true" o "false" (ignorando mayúsculas/minúsculas)
        String solucionStr = datos.solucion().trim();
        if (!solucionStr.equalsIgnoreCase("true") && !solucionStr.equalsIgnoreCase("false")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'solucion' solo puede ser 'true' o 'false'.");
        }
        Boolean solucion = Boolean.parseBoolean(solucionStr);

        // Evitar duplicados: verificar si ya existe una respuesta con el mismo mensaje, autor y tópico
        if (respuestaRepository.existsByMensajeAndAutorAndTopico(datos.mensaje(), datos.autor(), topico)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una respuesta idéntica para este tópico.");
        }

        // Crear y configurar la nueva entidad Respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setFechaCreacion(LocalDateTime.now()); // Fecha/hora actual
        respuesta.setAutor(datos.autor());
        respuesta.setSolucion(solucion);
        respuesta.setTopico(topico);

        // Asegurar que el campo 'activo' esté inicializado (por defecto en true si es null)
        if (respuesta.getActivo() == null) {
            respuesta.setActivo(true);
        }

        // Guardar la respuesta en la base de datos
        Respuesta savedRespuesta = respuestaRepository.save(respuesta);

        // Devolver la respuesta en formato DTO con estado 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADto(savedRespuesta));
    }

    /**
     * Endpoint GET para obtener todas las respuestas asociadas a un tópico específico.
     *
     * @param idTopico ID del tópico cuyas respuestas se desean consultar.
     * @return ResponseEntity con una lista de respuestas en formato DTO y estado HTTP 200 (OK).
     * @throws ResponseStatusException si el tópico no existe.
     */
    @GetMapping("/{idTopico}/respuestas")
    public ResponseEntity<List<DatosDetalleRespuesta>> obtenerRespuestas(@PathVariable Long idTopico) {
        // Verificar que el tópico exista
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Obtener todas las respuestas asociadas al tópico
        List<Respuesta> respuestas = respuestaRepository.findByTopico(topico);

        // Convertir cada entidad Respuesta a su DTO correspondiente
        List<DatosDetalleRespuesta> dtos = respuestas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Método auxiliar para convertir una entidad Respuesta a su DTO de detalle.
     *
     * @param respuesta Entidad Respuesta a convertir.
     * @return DTO con los datos relevantes de la respuesta.
     */
    private DatosDetalleRespuesta convertirADto(Respuesta respuesta) {
        return new DatosDetalleRespuesta(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getAutor(),
                respuesta.isSolucion(),
                respuesta.getTopico().getId()
        );
    }

    /**
     * Endpoint GET para obtener todas las respuestas marcadas como "solución" para un tópico dado.
     *
     * @param topicoId ID del tópico del cual se quieren las soluciones.
     * @return ResponseEntity con una lista de respuestas marcadas como solución (DTO) y estado HTTP 200 (OK).
     * @throws ResponseStatusException si el tópico no existe.
     */
    @GetMapping("/soluciones/{topicoId}")
    public ResponseEntity<List<DatosDetalleRespuesta>> obtenerSoluciones(@PathVariable Long topicoId) {
        // Validar que el tópico exista
        if (!topicoRepository.existsById(topicoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado.");
        }

        // Buscar respuestas donde 'solucion' sea true y pertenezcan al tópico especificado
        List<Respuesta> soluciones = respuestaRepository.findByTopicoIdAndSolucionTrue(topicoId);

        // Convertir a DTO
        List<DatosDetalleRespuesta> dtos = soluciones.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}