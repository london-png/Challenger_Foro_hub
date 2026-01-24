package med.voll.ForoHub.datos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DatosRegistroTopico(

        //se valida que todos los campos son obligatorios
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotBlank(message = "El autor es obligatorio")
        String autor,

        @NotNull(message = "El cursoId es obligatorio")
        //@Min(value = 1, message = "El cursoId debe ser un número entero positivo")
        String cursoId  // Solo el ID del curso, no el objeto completo
) {}