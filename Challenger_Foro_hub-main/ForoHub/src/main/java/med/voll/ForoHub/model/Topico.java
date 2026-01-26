package med.voll.ForoHub.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import med.voll.ForoHub.datos.DatosActualizacionTopico;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Indica que esta clase es una entidad JPA y se mapeará a la tabla "topico" en la base de datos.
// Se define una restricción única compuesta por las columnas "titulo" y "mensaje" para evitar duplicados.
@Entity
@Table(name = "topico",
        uniqueConstraints = @UniqueConstraint(columnNames = {"titulo", "mensaje"}) )
// @EqualsAndHashCode se configura para incluir solo los campos marcados explícitamente con @EqualsAndHashCode.Include
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// Lombok: genera automáticamente getters, setters, toString(), equals(), hashCode() y constructor requerido.
@Data
// Lombok: genera un constructor sin argumentos (necesario para JPA).
@NoArgsConstructor
public class Topico {

    // Campo incluido explícitamente en equals() y hashCode().
    @EqualsAndHashCode.Include
    // Define el campo como clave primaria.
    @Id
    // Estrategia de generación automática del ID (auto-incremental en MySQL).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bandera para lógica de eliminación suave (soft delete); por defecto activo.
    private boolean activo = true;

    // Título del tópico.
    private String titulo;

    // Mensaje o contenido principal del tópico.
    private String mensaje;

    // Fecha y hora de creación del tópico.
    private LocalDateTime fechaCreacion;

    // Enumeración que representa el estado del tópico (ej. ABIERTO, CERRADO, etc.).
    @Enumerated(EnumType.STRING) // Almacena el nombre del enum como String en la BD.
    private Status status;

    // Nombre del autor del tópico.
    private String autor;

    // Relación Muchos-a-Uno con la entidad Curso: varios tópicos pertenecen a un curso.
    // FetchType.LAZY: el curso no se carga hasta que se acceda explícitamente.
    @ManyToOne(fetch = FetchType.LAZY)
    // La columna foránea en la tabla "topico" será "curso_id".
    @JoinColumn(name = "curso_id")
    private Curso curso;

    // Relación Uno-a-Muchos con Respuesta: un tópico puede tener múltiples respuestas.
    // mappedBy = "topico": indica que el lado propietario de la relación está en la entidad Respuesta.
    // cascade = CascadeType.ALL: operaciones en el tópico se propagan a sus respuestas.
    // orphanRemoval = true: si una respuesta se desvincula del tópico, se elimina automáticamente.
    @OneToMany(mappedBy = "topico", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas = new ArrayList<>();

    // Constructor explícito para inicializar un objeto Topico con todos sus atributos.
    // Útil para pruebas o inyección manual.
    public Topico(Long id, String titulo, String mensaje, LocalDateTime fechaCreacion, Status status, String autor, Curso curso, List<Respuesta> respuestas) {
        this.id = id;
        this.activo = true; // Siempre se inicia como activo.
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
        this.status = status;
        this.autor = autor;
        this.curso = curso;
        this.respuestas = respuestas != null ? respuestas : new ArrayList<>();
    }

    // Método para actualizar selectivamente los campos del tópico usando un DTO de actualización.
    // Solo se actualizan los campos que no sean null en el DTO.
    public void actualizarInformacion(@Valid DatosActualizacionTopico datos) {
        if (datos.titulo() != null) {
            this.titulo = datos.titulo();
        }
        if (datos.mensaje() != null) {
            this.mensaje = datos.mensaje();
        }
        if (datos.fechaCreacion() != null) {
            this.fechaCreacion = datos.fechaCreacion();
        }
        // Nota: la validación de fechaCreacion está duplicada; probablemente sea redundante.
        if (datos.fechaCreacion() != null) {
            this.fechaCreacion = datos.fechaCreacion();
        }
        if (datos.status() != null) {
            this.status = datos.status();
        }
        if (datos.autor() != null) {
            this.autor = datos.autor();
        }
    }

    // Método setter para el campo 'activo', usado en lógica de eliminación suave.
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}