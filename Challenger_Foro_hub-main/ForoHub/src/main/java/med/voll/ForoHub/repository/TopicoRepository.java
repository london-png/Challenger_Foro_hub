package med.voll.ForoHub.repository;

import med.voll.ForoHub.model.Topico;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Incluye operaciones CRUD básicas para Topico (ID de tipo Long)
@Where(clause = "activo = true")
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    // Método para buscar por nombre del curso (insensible a mayúsculas) y año
    @Query("SELECT t FROM Topico t WHERE " +
            "t.activo = true AND " +
            "( :nombreCurso IS NULL OR LOWER(t.curso.nombre) = LOWER(:nombreCurso) ) AND " +
            "( :ano IS NULL OR YEAR(t.fechaCreacion) = :ano )")
    Page<Topico> findByCursoNombreAndAno(
            @Param("nombreCurso") String nombreCurso,
            @Param("ano") Integer ano,
            Pageable pageable);

    //para consultar todos los topicos solucionados con solucion =true
    @Query("SELECT DISTINCT t FROM Topico t LEFT JOIN FETCH t.respuestas r WHERE r.solucion = true")
    Page<Topico> findAllWithSolucion(Pageable pageable);
}