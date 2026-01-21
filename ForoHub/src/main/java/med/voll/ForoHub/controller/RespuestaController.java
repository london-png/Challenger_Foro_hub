

package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.datos.DatosRespuesta;
import med.voll.ForoHub.model.Respuesta;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.RespuestaRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/topicos")
public class RespuestaController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    // POST /topicos/{idTopico}/respuestas
    @PostMapping("/{idTopico}/respuestas")
    public ResponseEntity<Respuesta> agregarRespuesta(
            @PathVariable Long idTopico,
            @RequestBody @Valid DatosRespuesta datos) {

        //  Buscar el tópico al que se va a responder
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        // Crear la nueva respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setFechaCreacion(datos.fechaCreacion() != null ? datos.fechaCreacion() : LocalDateTime.now());
        respuesta.setAutor(datos.autor());
        respuesta.setSolucion(datos.solucion() != null ? datos.solucion() : false);
        respuesta.setTopico(topico); // Establece la relación

        // Guardar en base de datos
        respuesta = respuestaRepository.save(respuesta);

        //Devolver con código 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Opcional: Obtener todas las respuestas de un tópico
    @GetMapping("/{idTopico}/respuestas")
    public ResponseEntity<List<Respuesta>> obtenerRespuestas(@PathVariable Long idTopico) {
        Topico topico = topicoRepository.findById(idTopico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));

        List<Respuesta> respuestas = respuestaRepository.findByTopico(topico);
        return ResponseEntity.ok(respuestas);
    }
}