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

@RestController
@RequestMapping("/topicos")
public class RespuestaController {

    private final TopicoRepository topicoRepository;
    private final RespuestaRepository respuestaRepository;

    public RespuestaController(TopicoRepository topicoRepository, RespuestaRepository respuestaRepository) {
        this.topicoRepository = topicoRepository;
        this.respuestaRepository = respuestaRepository;
    }

    @PostMapping("/{idTopico}/respuestas")
    public ResponseEntity<DatosDetalleRespuesta> agregarRespuesta(
            @PathVariable Long idTopico,
            @RequestBody @Valid DatosRespuesta datos) {

        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setFechaCreacion(LocalDateTime.now());
        respuesta.setAutor(datos.autor());
        respuesta.setSolucion(datos.solucion() != null ? datos.solucion() : false);
        respuesta.setTopico(topico);

        if (respuesta.getActivo() == null) {
            respuesta.setActivo(true);
        }

        Respuesta savedRespuesta = respuestaRepository.save(respuesta);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADto(savedRespuesta));
    }

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

    private DatosDetalleRespuesta convertirADto(Respuesta respuesta) {
        return new DatosDetalleRespuesta(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getAutor(),
                respuesta.isSolucion(), // ✅ Usa isSolucion() para booleanos
                respuesta.getTopico().getId()
        );
    }
    //Get para consultar los topicos ya solucionados
    @GetMapping("/soluciones/{topicoId}")
    public ResponseEntity<List<DatosDetalleRespuesta>> obtenerSoluciones(@PathVariable Long topicoId) {
        // Verificar que el tópico exista
        if (!topicoRepository.existsById(topicoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado.");
        }

        List<Respuesta> soluciones = respuestaRepository.findByTopicoIdAndSolucionTrue(topicoId);

        List<DatosDetalleRespuesta> dtos = soluciones.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}