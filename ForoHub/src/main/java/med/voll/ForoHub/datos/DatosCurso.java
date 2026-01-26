package med.voll.ForoHub.datos;

/**
 * DTO (Objeto de Transferencia de Datos) que representa la información básica de un curso.
 * Se utiliza para evitar exponer entidades directamente en la capa de controlador
 * y para limitar los datos enviados o recibidos en las operaciones de la API.
 *
 * @param id        Identificador único del curso en la base de datos.
 * @param nombre    Nombre del curso (por ejemplo: "Spring Boot Avanzado").
 * @param categoria Categoría a la que pertenece el curso (por ejemplo: "Desarrollo Backend").
 */
public record DatosCurso(
        Long id,
        String nombre,
        String categoria
) {}