package med.voll.ForoHub.repository;

import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.domain.Topico;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

//Repositorio JPA para la entidad {@link Topico}.
// Operaciones CRUD básicas.
// Búsqueda por curso y año.
// Filtrado de tópicos con estado RESUELTO.
// Carga explícita de respuestas al obtener un tópico por ID.

@Where(clause = "activo = true")
public interface TopicoRepository extends JpaRepository<Topico, Long> {

     //Verifica si ya existe un tópico con el mismo título y mensaje.

    boolean existsByTituloAndMensaje(String titulo, String mensaje);

     // Busca tópicos por nombre de curso (insensible a mayúsculas) y año.

    @Query("SELECT t FROM Topico t WHERE " +
            "t.activo = true AND " +
            "( :nombreCurso IS NULL OR LOWER(t.curso.nombre) = LOWER(:nombreCurso) ) AND " +
            "( :ano IS NULL OR YEAR(t.fechaCreacion) = :ano )")
    Page<Topico> findByCursoNombreAndAno(
            @Param("nombreCurso") String nombreCurso,
            @Param("ano") Integer ano,
            Pageable pageable);

     // Obtiene todos los tópicos con estado RESUELTO (no solo con respuestas marcadas como solución)
     // Este es el cambio CLAVE: Filtra explícitamente por estado RESUELTO,
     // no por respuestas marcadas como solución.

    @Query("SELECT t FROM Topico t WHERE t.status = :status")
    Page<Topico> findAllWithSolucion(
            @Param("status") Status status,  // Parámetro para filtrar por estado
            Pageable pageable
    );

     // Sobrescribe el método findById para cargar las respuestas asociadas al tópico.
     // Esto es necesario para que la regla de negocio "No se puede marcar como resuelto sin respuestas"
     // funcione correctamente en TopicoService.

    @EntityGraph(attributePaths = "respuestas")
    Optional<Topico> findById(Long id);
}