package med.voll.ForoHub.service;

import med.voll.ForoHub.datos.DatosActualizacionTopico;
import med.voll.ForoHub.datos.DatosDetalleTopico;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import med.voll.ForoHub.model.Curso;
import med.voll.ForoHub.model.Respuesta;
import med.voll.ForoHub.model.Status;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.CursoRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.*;

/**
 * Servicio encargado de la lógica de negocio relacionada con los Tópicos.
 *
 * Responsabilidades:
 * - Validar datos de entrada (como cursoId).
 * - Prevenir duplicados.
 * - Gestionar la creación, actualización y eliminación lógica de tópicos.
 * - Convertir entidades a DTOs de salida.
 * - Obtener tópicos con su solución asociada.
 *
 * ✅ El controlador solo orquesta; toda la lógica compleja vive aquí.
 */
@Service
public class TopicoService {

    // === Inyección de dependencias ===
    private final TopicoRepository topicoRepository;
    private final CursoRepository cursoRepository;

    public TopicoService(TopicoRepository topicoRepository, CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Registra un nuevo tópico en el sistema.
     *
     * @param datos DTO con los datos del tópico a crear.
     * @return {@link DatosDetalleTopico} con la información completa del tópico creado.
     */
    @Transactional
    public DatosDetalleTopico registrar(DatosRegistroTopico datos) {
        // Validar que cursoId sea un número entero positivo
        validarCursoId(datos.cursoId());

        // Evitar duplicados: mismo título y mensaje ya existen
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un tópico con ese título y mensaje.");
        }

        // Buscar el curso asociado
        Long cursoId = parseCursoId(datos.cursoId());
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Curso no encontrado"));

        // Crear nueva instancia de Topico
        Topico topico = new Topico(
                null,                         // ID será generado por la base de datos
                datos.titulo(),
                datos.mensaje(),
                LocalDateTime.now(),          // Fecha de creación actual
                Status.ABIERTO,               // Estado inicial
                datos.autor(),
                curso,
                new ArrayList<>()             // Lista vacía de respuestas
        );

        // Guardar en base de datos
        Topico savedTopico = topicoRepository.save(topico);

        // Devolver DTO con los datos completos
        return toDatosDetalleTopico(savedTopico);
    }

    /**
     * Obtiene los detalles de un tópico por su ID.
     *
     * ⚠️ Este método NO carga las respuestas asociadas. Si necesitas la solución,
     * usa {@link #obtenerPorIdConSolucion(Long)}.
     *
     * @param id ID del tópico.
     * @return {@link DatosDetalleTopico} con la información del tópico (sin solución).
     */
    @Transactional(readOnly = true)
    public DatosDetalleTopico obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El ID del tópico es obligatorio y debe ser válido.");
        }

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tópico no encontrado."));

        return toDatosDetalleTopico(topico);
    }

    /**
     * Obtiene los detalles de un tópico por su ID, incluyendo la solución si existe.
     *
     * ✅ Este método es el que debes usar cuando quieras ver un tópico solucionado
     * con toda su información (como en el endpoint "/topicos/con-solucion").
     *
     * @param id ID del tópico.
     * @return {@link DatosDetalleTopico} con la información del tópico y su solución.
     */
    @Transactional(readOnly = true)
    public DatosDetalleTopico obtenerPorIdConSolucion(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El ID del tópico es obligatorio y debe ser válido.");
        }

        // ⚠️ IMPORTANTE: Asegúrate de que tu repositorio cargue las respuestas.
        // Si usas @EntityGraph o JOIN FETCH, este método funcionará correctamente.
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tópico no encontrado."));

        // Extraer la solución si existe (busca la primera respuesta con solucion = true)
        String solucion = topico.getRespuestas().stream()
                .filter(Respuesta::isSolucion)      // Usa isSolucion() porque el campo es 'boolean solucion'
                .findFirst()
                .map(Respuesta::getMensaje)         // Tu campo se llama 'mensaje', no 'contenido'
                .orElse(null);                      // Si no hay solución, devuelve null

        // Construir DTO con todos los datos + la solución
        return new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso().getNombre(),
                solucion  // 👈 Campo nuevo: la solución asociada al tópico
        );
    }

    /**
     * Actualiza un tópico existente de forma parcial (solo campos no nulos).
     *
     * @param datos DTO con los campos a actualizar.
     * @return {@link DatosDetalleTopico} actualizado.
     */
    @Transactional
    public DatosDetalleTopico actualizar(DatosActualizacionTopico datos) {
        if (datos.id() == null || datos.id() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El campo 'id' es obligatorio y debe ser positivo.");
        }

        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tópico no encontrado."));

        // Actualizar solo los campos proporcionados (no nulos)
        if (datos.titulo() != null) topico.setTitulo(datos.titulo());
        if (datos.mensaje() != null) topico.setMensaje(datos.mensaje());
        if (datos.fechaCreacion() != null) topico.setFechaCreacion(datos.fechaCreacion());
        if (datos.status() != null) topico.setStatus(datos.status());
        if (datos.autor() != null) topico.setAutor(datos.autor());

        // Si se proporciona cursoId, validarlo y actualizar el curso
        if (datos.cursoId() != null) {
            validarCursoId(datos.cursoId());
            Long cursoId = parseCursoId(datos.cursoId());
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Curso no encontrado."));
            topico.setCurso(curso);
        }

        topicoRepository.save(topico);
        return toDatosDetalleTopico(topico);
    }

    /**
     * Elimina lógicamente un tópico (soft delete).
     *
     * @param id ID del tópico a eliminar.
     */
    @Transactional
    public void eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El ID del tópico es obligatorio.");
        }

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tópico no encontrado."));

        topico.setActivo(false);
        topicoRepository.save(topico);
    }

    // ────────────────────────────────────────────────────────
    // Métodos auxiliares privados
    // ────────────────────────────────────────────────────────

    /**
     * Convierte una cadena de texto en un Long (para cursoId).
     * Lanza excepción si no es un número válido.
     */
    private Long parseCursoId(String cursoIdStr) {
        try {
            return Long.parseLong(cursoIdStr.trim());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(BAD_REQUEST, "El cursoId debe ser un número entero válido.");
        }
    }

    /**
     * Valida que el cursoId sea un número entero positivo.
     */
    private void validarCursoId(String cursoIdStr) {
        Long id = parseCursoId(cursoIdStr);
        if (id <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El cursoId debe ser un número entero positivo.");
        }
    }

    /**
     * Convierte una entidad {@link Topico} en un DTO de salida {@link DatosDetalleTopico}.
     *
     * ⚠️ Este método NO incluye la solución. Se usa en operaciones donde no se necesita.
     */
    private DatosDetalleTopico toDatosDetalleTopico(Topico topico) {
        return new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso().getNombre(),
                null
        );
    }
}