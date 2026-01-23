package med.voll.ForoHub.repository;

import med.voll.ForoHub.model.Topico;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//con esta linea ya incluye todo lo de un CRUD<Topico, Long> la clase que se va a tomar, long que es el id de esa clase
@Where(clause = "activo = true")
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensaje(String titulo, String mensaje);


    //metodo para buscar por nombre del curso y el año
    @Query("SELECT t FROM Topico t WHERE " +
            "t.activo = true AND " +
            "( :nombreCurso IS NULL OR LOWER(t.curso.nombre) = LOWER(:nombreCurso) ) AND " +
            "( :ano IS NULL OR YEAR(t.fechaCreacion) = :ano )")
    Page<Topico> findByCursoNombreAndAno(
            @Param("nombreCurso") String nombreCurso,
            @Param("ano") Integer ano,
            Pageable pageable);
}
