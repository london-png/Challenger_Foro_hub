package med.voll.ForoHub.repository;

import med.voll.ForoHub.domain.Curso;
import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.domain.Topico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TopicoRepositoryTest {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // === PRUEBA 1: existsByTituloAndMensaje ===
    @Test
    @DisplayName("Debe retornar true cuando existe un tópico con el mismo título y mensaje")
    void existsByTituloAndMensaje_WhenExists_ReturnsTrue() {
        // Given
        Topico topico = new Topico();
        topico.setTitulo("¿Cómo usar Spring Boot?");
        topico.setMensaje("Estoy aprendiendo Spring Boot.");
        topico.setFechaCreacion(LocalDateTime.now());
        topico.setStatus(Status.ABIERTO);
        topico.setAutor("Carlos Palacios");
        topicoRepository.save(topico);

        // When
        boolean existe = topicoRepository.existsByTituloAndMensaje(
                "¿Cómo usar Spring Boot?",
                "Estoy aprendiendo Spring Boot."
        );

        // Then
        assertTrue(existe);
    }

    @Test
    @DisplayName("Debe retornar false cuando no existe un tópico con ese título y mensaje")
    void existsByTituloAndMensaje_WhenNotExists_ReturnsFalse() {
        // Given: no hay ningún tópico con estos datos

        // When
        boolean existe = topicoRepository.existsByTituloAndMensaje(
                "Título inexistente",
                "Mensaje inexistente"
        );

        // Then
        assertFalse(existe);
    }

    // === PRUEBA 2: findByCursoNombreAndAno ===
    @Test
    @DisplayName("Debe filtrar tópicos por nombre de curso (insensible a mayúsculas) y año")
    void findByCursoNombreAndAno_WithValidFilters_ReturnsMatchingTopics() {
        // Given
        Curso curso = new Curso();
        curso.setNombre("Spring Boot");
        curso.setCategoria("Backend");
        Curso cursoGuardado = cursoRepository.save(curso);

        Topico topico1 = new Topico();
        topico1.setTitulo("Pregunta 1");
        topico1.setMensaje("...");
        topico1.setFechaCreacion(LocalDateTime.of(2025, 6, 15, 10, 0));
        topico1.setStatus(Status.ABIERTO);
        topico1.setAutor("Usuario1");
        topico1.setCurso(cursoGuardado);

        Topico topico2 = new Topico();
        topico2.setTitulo("Pregunta 2");
        topico2.setMensaje("...");
        topico2.setFechaCreacion(LocalDateTime.of(2026, 1, 10, 9, 0)); // Año diferente
        topico2.setStatus(Status.ABIERTO);
        topico2.setAutor("Usuario2");
        topico2.setCurso(cursoGuardado);

        topicoRepository.save(topico1);
        topicoRepository.save(topico2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var resultado = topicoRepository.findByCursoNombreAndAno("spring boot", 2025, pageable);

        // Then
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Pregunta 1", resultado.getContent().get(0).getTitulo());
    }

    @Test
    @DisplayName("Debe ignorar el filtro de curso si es null")
    void findByCursoNombreAndAno_WithNullCurso_ReturnsAllByYear() {
        // Given
        Curso curso1 = new Curso();
        curso1.setNombre("Java");
        curso1.setCategoria("Backend");
        Curso c1 = cursoRepository.save(curso1);

        Curso curso2 = new Curso();
        curso2.setNombre("Python");
        curso2.setCategoria("Backend");
        Curso c2 = cursoRepository.save(curso2);

        Topico t1 = crearTopico("T1", "M1", LocalDateTime.of(2025, 3, 1, 10, 0), c1);
        Topico t2 = crearTopico("T2", "M2", LocalDateTime.of(2025, 4, 1, 10, 0), c2);

        topicoRepository.save(t1);
        topicoRepository.save(t2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var resultado = topicoRepository.findByCursoNombreAndAno(null, 2025, pageable);

        // Then
        assertEquals(2, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Debe ignorar el filtro de año si es null")
    void findByCursoNombreAndAno_WithNullYear_ReturnsAllByCurso() {
        // Given
        Curso curso = new Curso();
        curso.setNombre("Docker");
        curso.setCategoria("DevOps");
        Curso c = cursoRepository.save(curso);

        Topico t1 = crearTopico("T1", "M1", LocalDateTime.of(2024, 1, 1, 10, 0), c);
        Topico t2 = crearTopico("T2", "M2", LocalDateTime.of(2025, 1, 1, 10, 0), c);

        topicoRepository.save(t1);
        topicoRepository.save(t2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var resultado = topicoRepository.findByCursoNombreAndAno("docker", null, pageable);

        // Then
        assertEquals(2, resultado.getTotalElements());
    }

    // Método auxiliar
    private Topico crearTopico(String titulo, String mensaje, LocalDateTime fecha, Curso curso) {
        Topico t = new Topico();
        t.setTitulo(titulo);
        t.setMensaje(mensaje);
        t.setFechaCreacion(fecha);
        t.setStatus(Status.ABIERTO);
        t.setAutor("Test User");
        t.setCurso(curso);
        return t;
    }
}