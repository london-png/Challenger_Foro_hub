package med.voll.ForoHub.repository;

import med.voll.ForoHub.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

//con esta linea ya incluye todo lo de un CRUD<Topico, Long> la clase que se va a tomar, long que es el id de esa clase
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensaje(String titulo, String mensaje);
}
