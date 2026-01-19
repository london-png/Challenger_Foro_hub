package med.voll.ForoHub.topico;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "topico") // en Java (dentro del contexto de Jakarta Persistence JPA esta entidad se guardda en a tabla topico
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
    private List<Respuesta> respuestas;

    // Constructor opcional (útil para servicios o pruebas)
    public Topico(String titulo, String mensaje, LocalDateTime fechaCreacion, Status status, String autor, Curso curso) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
        this.status = status;
        this.autor = autor;
        this.curso = curso;
    }
}
