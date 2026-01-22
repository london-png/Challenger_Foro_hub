package med.voll.ForoHub.datos;

import med.voll.ForoHub.model.Status;

import java.time.LocalDateTime;

public record DatosDetalleTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Status status,
        String autor,
        String nombreCurso
) {}
