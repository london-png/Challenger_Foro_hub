package med.voll.ForoHub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Entidad JPA que representa un curso en el sistema.
 *
 * <p>Un curso agrupa tópicos del foro por área temática (ej. "Spring Boot", "Java Avanzado").
 * Se relaciona con la entidad {@link Topico} mediante una relación uno a muchos.</p>
 */
@Entity
@Table(name = "curso",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nombre", "categoria"})
        }
)
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Curso {

    /**
     * Identificador único del curso, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del curso (ej. "Fundamentos de Spring Boot").
     * No puede ser nulo ni vacío.
     */
    @NonNull
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Categoría del curso (ej. "Desarrollo Backend").
     * No puede ser nulo ni vacío.
     */
    @NonNull
    @Column(nullable = false, length = 100)
    private String categoria;
}