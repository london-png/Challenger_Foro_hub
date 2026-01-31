package med.voll.ForoHub.datos;

public record DatosSolucionTopico(
        Long topicoId,
        String solucion,
        String mensaje,
        String autor
) {}