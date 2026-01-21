package med.voll.ForoHub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topico",
       uniqueConstraints = @UniqueConstraint(columnNames = {"titulo", "mensaje"}) ) // en Java (dentro del contexto de Jakarta Persistence JPA esta entidad se guardda en a tabla topico

//@Entity(name = "Topico") // indica que la clase es una entidad JPA.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data //incluye (Getter, Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor)
@NoArgsConstructor
//creamos la entidad JPA(Java Persistence API)
public class Topico {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String autor;

    // Relación muchos-a-uno con Curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    // Relación uno-a-muchos con Respuesta (bidireccional)
    @OneToMany(mappedBy = "topico", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas = new ArrayList<>();

    //Constructor explícito para uso en el controlador
    public Topico(Long id, String titulo, String mensaje, LocalDateTime fechaCreacion, Status status, String autor, Curso curso, List<Respuesta> respuestas) {
        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
        this.status = status;
        this.autor = autor;
        this.curso = curso;
        this.respuestas = respuestas != null ? respuestas : new ArrayList<>();
    }
}
