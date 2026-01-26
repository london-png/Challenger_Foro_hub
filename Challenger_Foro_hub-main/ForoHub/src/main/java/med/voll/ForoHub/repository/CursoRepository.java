package med.voll.ForoHub.repository;

import med.voll.ForoHub.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Verifica si existe un curso con el nombre exacto (sensible a tildes y mayúsculas)
    boolean existsByNombre(String nombre);

    //Verifica si existe un curso ignorando mayúsculas/minúsculas
    boolean existsByNombreIgnoreCase(String nombre);
}