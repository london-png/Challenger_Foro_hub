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
 * Controlador REST para gestionar operaciones CRUD sobre las Respuestas.
 *
 * ✅ Responsabilidad: Gestionar respuestas asociadas a tópicos.
 * ❌ No maneja la lógica de "ver tópico con solución" → eso pertenece a TopicoController.
 */
@RestController
@RequestMapping("/topicos")
public class RespuestaController {

    private final TopicoRepository topicoRepository;
    private final RespuestaRepository respuestaRepository;

    public RespuestaController(TopicoRepository topicoRepository, RespuestaRepository respuestaRepository) {
        this.topicoRepository = topicoRepository;
        this.respuestaRepository = respuestaRepository;
    }

    /**
     * Agrega una nueva respuesta a un tópico específico.
     *
     * @param idTopico ID del tópico al cual se añade la respuesta.
     * @param datos DTO con los datos de la respuesta.
     * @return ResponseEntity con la respuesta creada y estado 201 Created.
     */
    @PostMapping("/{idTopico}/respuestas")
    public ResponseEntity<DatosDetalleRespuesta> agregarRespuesta(
            @PathVariable Long idTopico,
            @RequestBody @Valid DatosRespuesta datos) {

        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // === Validar que 'solucion' sea "true" o "false" (ignorando mayúsculas) ===
        String solucionStr = datos.solucion().trim();
        if (!solucionStr.equalsIgnoreCase("true") && !solucionStr.equalsIgnoreCase("false")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'solucion' solo puede ser 'true' o 'false'.");
        }
        Boolean solucion = Boolean.parseBoolean(solucionStr);

        // === Validar que no exista una respuesta duplicada ===
        if (respuestaRepository.existsByMensajeAndAutorAndTopico(datos.mensaje(), datos.autor(), topico)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una respuesta idéntica para este tópico.");
        }

        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setFechaCreacion(LocalDateTime.now());
        respuesta.setAutor(datos.autor());
        respuesta.setSolucion(solucion);
        respuesta.setTopico(topico);

        if (respuesta.getActivo() == null) {
            respuesta.setActivo(true);
        }

        Respuesta savedRespuesta = respuestaRepository.save(respuesta);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADto(savedRespuesta));
    }

    /**
     * Obtiene todas las respuestas asociadas a un tópico.
     *
     * @param idTopico ID del tópico.
     * @return ResponseEntity con la lista de respuestas.
     */
    @GetMapping("/{idTopico}/respuestas")
    public ResponseEntity<List<DatosDetalleRespuesta>> obtenerRespuestas(@PathVariable Long idTopico) {
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        List<Respuesta> respuestas = respuestaRepository.findByTopico(topico);

        List<DatosDetalleRespuesta> dtos = respuestas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Convierte una entidad {@link Respuesta} en un DTO de salida {@link DatosDetalleRespuesta}.
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

    // ⚠️ El método "/soluciones/{topicoId}" ha sido eliminado para evitar conflicto con TopicoController.
    // ✅ La funcionalidad de "ver tópico con solución" ahora está en TopicoController.
}