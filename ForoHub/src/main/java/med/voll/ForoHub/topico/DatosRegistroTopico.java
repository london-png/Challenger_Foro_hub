package med.voll.ForoHub.topico;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;


public record DatosRegistroTopico(
        Long idUsuario,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        DatosCurso curso,
        @JsonProperty("Respuestas")//e utiliza en aplicaciones Java para controlar cómo se serializan y deserializan los campos de una clase a/desde JSON
        List<DatosRespuesta> respuestas

) {
}

