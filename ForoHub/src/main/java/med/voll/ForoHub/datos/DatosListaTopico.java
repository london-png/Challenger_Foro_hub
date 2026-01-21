package med.voll.ForoHub.datos;

import med.voll.ForoHub.model.Status;
import med.voll.ForoHub.model.Topico;

import java.time.LocalDateTime;

public record DatosListaTopico(
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        Long cursoId,
        String nombreCurso,
        String categoriaCurso
) {
    public DatosListaTopico(Topico topico) {
        this(
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso() != null ? topico.getCurso().getId() : null,
                topico.getCurso() != null ? topico.getCurso().getNombre() : null,
                topico.getCurso() != null ? topico.getCurso().getCategoria() : null
        );
    }
}