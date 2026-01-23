// src/main/java/med.voll.ForoHub/topico/RespuestaRepository.java

package med.voll.ForoHub.repository;

import med.voll.ForoHub.model.Respuesta;
import med.voll.ForoHub.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // indica  que es un componente de acceso a datos
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // Método para encontrar todas las respuestas de un tópico (opcional, pero útil)
    List<Respuesta> findByTopico(Topico topico);

    // método: obtener solo las soluciones
    List<Respuesta> findByTopicoIdAndSolucionTrue(Long topicoId);

}