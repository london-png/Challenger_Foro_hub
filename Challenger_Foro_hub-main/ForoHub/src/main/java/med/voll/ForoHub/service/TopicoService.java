package med.voll.ForoHub.service;

import med.voll.ForoHub.datos.DatosActualizacionTopico;
import med.voll.ForoHub.datos.DatosDetalleTopico;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import med.voll.ForoHub.datos.DatosRespuesta;
import med.voll.ForoHub.domain.Curso;
import med.voll.ForoHub.domain.Respuesta;
import med.voll.ForoHub.domain.Status;
import med.voll.ForoHub.domain.Topico;
import med.voll.ForoHub.repository.CursoRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import med.voll.ForoHub.rules.business.TopicoRules; // 👈 IMPORTACIÓN DE REGLAS
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
 * - ✅ Validar reglas de negocio al escribir soluciones (mensaje y autor obligatorios)
 * - ✅ Actualizar estado del tópico a RESUELTO al escribir una solución
 * - ✅ Aplicar reglas de negocio mediante TopicoRules
 *
 * ✅ El controlador solo orquesta; toda la lógica compleja vive aquí.
 */
@Service
public class TopicoService {

    // === Inyección de dependencias ===
    private final TopicoRepository topicoRepository;
    private final CursoRepository cursoRepository;
    private final TopicoRules topicoRules; // 👈 INYECCIÓN DE REGLAS DE NEGOCIO

    // ✅ INYECCIÓN DE ENTITY MANAGER PARA FORZAR DETECCIÓN DE CAMBIOS
    @PersistenceContext
    private EntityManager entityManager;

    // Constructor con inyección de TopicoRules
    public TopicoService(TopicoRepository topicoRepository, CursoRepository cursoRepository, TopicoRules topicoRules) {
        this.topicoRepository = topicoRepository;
        this.cursoRepository = cursoRepository;
        this.topicoRules = topicoRules;
    }

    /**
     * Registra un nuevo tópico en el sistema.
     *
     * @param datos DTO con los datos del tópico a crear.
     * @return {@link DatosDetalleTopico} con la información completa del tópico creado.
     */
    @Transactional
    public DatosDetalleTopico registrar(DatosRegistroTopico datos) {
        // ✅ APLICAR REGLAS DE NEGOCIO: Validación de calidad del mensaje
        topicoRules.validarCalidadMensaje(datos.mensaje(), datos.titulo());

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

        //FORZAR LA CARGA DE LAS RESPUESTAS
        topico.getRespuestas().size();

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
     * ✅ ESCRIBE UNA RESPUESTA (SOLUCIÓN) PARA UN TÓPICO
     *
     * ⚠️ Reglas de negocio críticas:
     * - Si se marca como solución ("solucion": "True"), los campos 'mensaje' y 'autor' son OBLIGATORIOS
     * - Si falta alguno de estos campos, se lanza excepción 400 Bad Request
     * - ✅ Al marcar como solución, el estado del tópico CAMBIA AUTOMÁTICAMENTE a RESUELTO
     * - ✅ Un tópico solo puede tener una solución
     * - ✅ El autor del tópico no puede marcar su propia respuesta como solución
     *
     * @param topicoId ID del tópico al que se le agregará la respuesta
     * @param datos DTO con los datos de la respuesta
     * @return {@link DatosDetalleTopico} con la información actualizada del tópico
     */
    @Transactional
    public DatosDetalleTopico escribirRespuesta(Long topicoId, DatosRespuesta datos) {
        // ✅ VALIDACIÓN 1: Si es solución, mensaje es obligatorio
        if ("true".equalsIgnoreCase(datos.solucion()) &&
                (datos.mensaje() == null || datos.mensaje().isBlank())) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'mensaje' es obligatorio cuando se marca como solución."
            );
        }

        // ✅ VALIDACIÓN 2: Si es solución, autor es obligatorio
        if ("true".equalsIgnoreCase(datos.solucion()) &&
                (datos.autor() == null || datos.autor().isBlank())) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El campo 'autor' es obligatorio cuando se marca como solución."
            );
        }

        // ✅ BUSCAR EL TÓPICO
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tópico no encontrado."));

        // ✅ APLICAR REGLAS DE NEGOCIO: Validar calidad del mensaje
        topicoRules.validarCalidadMensaje(datos.mensaje(), null);

        // ✅ CONVERTIR A BOOLEANO
        boolean esSolucion = "true".equalsIgnoreCase(datos.solucion());

        // ✅ APLICAR REGLAS DE NEGOCIO: Validar autor si es solución
        if (esSolucion) {
            topicoRules.validarAutorSolucion(topico, datos.autor());
            topicoRules.validarUnicaSolucion(topico);
        }

        // ✅ CREAR LA RESPUESTA
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setAutor(datos.autor());
        respuesta.setFechaCreacion(LocalDateTime.now());
        respuesta.setTopico(topico);
        respuesta.setSolucion(esSolucion);

        // ✅ AGREGAR LA RESPUESTA AL TÓPICO
        topico.getRespuestas().add(respuesta);

        // ✅ SI ES SOLUCIÓN, ACTUALIZAR ESTADO A RESUELTO
        if (esSolucion) {
            topico.setStatus(Status.RESUELTO);
            System.out.println("✅ [AUTOMÁTICO] Estado del tópico ID " + topicoId + " CAMBIADO a: RESUELTO");
        }

        // ✅ GUARDAR Y FLUSH - PERSISTENCIA GARANTIZADA
        topicoRepository.saveAndFlush(topico);

        // ✅ LOG: Confirmar estado final
        System.out.println("💾 [FINAL] Tópico ID " + topicoId + " guardado con estado: " + topico.getStatus());

        // ✅ DEVOLVER EL TÓPICO CON SU INFORMACIÓN ACTUALIZADA
        return obtenerPorIdConSolucion(topicoId);
    }

    /**
     * ✅ MARCA UN TÓPICO COMO RESUELTO (SOLUCIONADO)
     *
     * Método dedicado para cambiar el estado de un tópico a RESUELTO
     * sin necesidad de crear una respuesta.
     *
     * ⚠️ Reglas de negocio:
     * - El tópico debe existir
     * - Solo se puede marcar como RESUELTO si está ABIERTO
     * - ✅ NO se verifica si ya hay una solución (para permitir actualización manual)
     *
     * @param id ID del tópico a marcar como solucionado
     * @return {@link DatosDetalleTopico} con la información actualizada del tópico
     */
    @Transactional
    public DatosDetalleTopico marcarComoSolucionado(Long id) {
        // ✅ VALIDACIÓN 1: Verificar que el ID sea válido
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El ID del tópico es obligatorio y debe ser un número positivo."
            );
        }

        // ✅ VALIDACIÓN 2: Buscar el tópico
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Tópico no encontrado con ID: " + id
                ));

        // ✅ VALIDACIÓN 3: Verificar si ya está resuelto
        if (topico.getStatus() == Status.RESUELTO) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "El tópico ya está marcado como RESUELTO."
            );
        }

        // ✅ LOG: Mostrar estado actual antes del cambio
        System.out.println("🔍 [ANTES] Estado actual del tópico ID " + id + ": " + topico.getStatus());
        System.out.println("🔍 [ANTES] Estado en BD: " + topicoRepository.findById(id).get().getStatus());

        // ✅ CAMBIAR EL ESTADO A RESUELTO
        topico.setStatus(Status.RESUELTO);

        // ✅ GUARDAR Y FLUSH PARA ASEGURAR QUE SE PERSISTE
        topicoRepository.saveAndFlush(topico);

        // ✅ LOG: Confirmar que se guardó
        System.out.println("✅ [CAMBIO] Estado del tópico ID " + id + " CAMBIADO a: RESUELTO");
        System.out.println("💾 [DESPUÉS] Tópico ID " + id + " GUARDADO en base de datos con estado: " + topico.getStatus());
        System.out.println("💾 [DESPUÉS] Estado en BD: " + topicoRepository.findById(id).get().getStatus());

        // ✅ DEVOLVER EL TÓPICO CON SU INFORMACIÓN ACTUALIZADA
        return obtenerPorIdConSolucion(id);
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

        // Si se actualiza el mensaje, aplicar validación de calidad
        if (datos.mensaje() != null) {
            topicoRules.validarCalidadMensaje(datos.mensaje(), null);
        }

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