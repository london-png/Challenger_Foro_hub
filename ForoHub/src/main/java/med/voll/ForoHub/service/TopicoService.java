// med.voll.ForoHub.service.TopicoService.java
package med.voll.ForoHub.service;

import med.voll.ForoHub.datos.DatosActualizacionTopico;
import med.voll.ForoHub.datos.DatosRegistroTopico;
import med.voll.ForoHub.datos.DatosTopicoConSolucion;
import med.voll.ForoHub.filtro.FiltroDatosTopico;
import med.voll.ForoHub.model.Curso;
import med.voll.ForoHub.model.Respuesta;
import med.voll.ForoHub.model.Status;
import med.voll.ForoHub.model.Topico;
import med.voll.ForoHub.repository.CursoRepository;
import med.voll.ForoHub.repository.TopicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio relacionada con los Tópicos.
 *
 * Responsabilidades:
 * - Validación de datos de entrada
 * - Creación, actualización y eliminación de tópicos
 * - Búsqueda de tópicos por filtros (curso y año)
 * - Verificación de duplicados y existencia de cursos
 * - Gestión de tópicos con soluciones asociadas
 */
@Service
public class TopicoService {

    // Repositorios necesarios para acceder a la base de datos
    private final TopicoRepository topicoRepository;
    private final CursoRepository cursoRepository;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param topicoRepository Repositorio de tópicos
     * @param cursoRepository Repositorio de cursos
     */
    public TopicoService(TopicoRepository topicoRepository, CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Registra un nuevo tópico en el sistema.
     *
     * @param datos Datos de registro del tópico
     * @return Entidad Topico lista para ser guardada
     */
    @Transactional
    public Topico registrar(DatosRegistroTopico datos) {
        // Validar que no exista un tópico con el mismo título y mensaje
        validarDuplicados(datos.titulo(), datos.mensaje());

        // Validar y convertir el ID del curso
        Long cursoId = validarCursoId(datos.cursoId());

        // Obtener el curso desde la base de datos
        Curso curso = obtenerCurso(cursoId);

        // Crear y devolver la nueva entidad Topico
        return new Topico(
                null,                           // ID será generado por la base de datos
                datos.titulo(),
                datos.mensaje(),
                LocalDateTime.now(),           // Fecha de creación actual
                Status.ABIERTO,               // Estado inicial
                datos.autor(),
                curso,                        // Curso asociado
                new ArrayList<>()             // Lista vacía de respuestas
        );
    }

    /**
     * Actualiza un tópico existente en el sistema.
     *
     * @param datos Datos de actualización del tópico
     * @return Entidad Topico actualizada
     */
    @Transactional
    public Topico actualizar(DatosActualizacionTopico datos) {
        // Buscar el tópico existente
        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));

        // Actualizar campos si se proporcionan nuevos valores
        if (datos.titulo() != null) topico.setTitulo(datos.titulo());
        if (datos.mensaje() != null) topico.setMensaje(datos.mensaje());
        if (datos.fechaCreacion() != null) topico.setFechaCreacion(datos.fechaCreacion());
        if (datos.status() != null) topico.setStatus(datos.status());
        if (datos.autor() != null) topico.setAutor(datos.autor());

        // Actualizar curso si se proporciona un nuevo cursoId
        if (datos.cursoId() != null) {
            Long cursoId = validarCursoId(datos.cursoId());
            Curso curso = obtenerCurso(cursoId);
            topico.setCurso(curso);
        }

        return topico;
    }

    /**
     * Busca tópicos por nombre de curso y año.
     *
     * @param filtros Filtros de búsqueda (nombreCurso y ano)
     * @param paginacion Configuración de paginación
     * @return Página de tópicos encontrados
     */
    public Page<Topico> buscarPorFiltros(FiltroDatosTopico filtros, Pageable paginacion) {
        // Validar y convertir el año
        Integer ano = validarAno(filtros.ano());
        String nombreCurso = filtros.nombreCurso().trim();

        // Realizar la búsqueda en la base de datos
        Page<Topico> topicos = topicoRepository.findByCursoNombreAndAno(nombreCurso, ano, paginacion);

        // Si no hay resultados, verificar si el curso existe
        if (topicos.isEmpty()) {
            verificarExistenciaCurso(nombreCurso);
            // Si el curso existe pero no hay tópicos, lanzar error 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron tópicos para este curso y año.");
        }

        return topicos;
    }

    /**
     * Marca un tópico como inactivo (eliminación lógica).
     *
     * @param id ID del tópico a eliminar
     */
    public void eliminar(Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado."));
        topico.setActivo(false);
    }

    /**
     * Obtiene todos los tópicos que tienen al menos una solución.
     *
     * Este método recupera tópicos que tienen respuestas marcadas como solución
     * y construye un DTO especial que incluye tanto la información del tópico
     * como la de su solución asociada.
     *
     * @param paginacion Configuración de paginación
     * @return Página de DTOs DatosTopicoConSolucion
     */
    public Page<DatosTopicoConSolucion> listarTopicosConSolucion(Pageable paginacion) {
        // Obtener tópicos que tienen al menos una solución
        Page<Topico> topicos = topicoRepository.findAllWithSolucion(paginacion);

        // Convertir cada tópico a su DTO correspondiente
        return topicos.map(topico -> {
            // Obtener la primera solución (asumiendo que solo hay una por tópico)
            var solucion = topico.getRespuestas().stream()
                    .filter(Respuesta::isSolucion)
                    .findFirst()
                    .orElse(null);

            // Si no hay solución, devolver DTO con campos de solución nulos
            if (solucion == null) {
                return new DatosTopicoConSolucion(
                        topico.getId(),
                        topico.getTitulo(),
                        topico.getMensaje(),
                        topico.getFechaCreacion(),
                        topico.getStatus().toString(),
                        topico.getAutor(),
                        topico.getCurso().getId(),
                        topico.getCurso().getNombre(),
                        topico.getCurso().getCategoria(),
                        null, null, null, null // Campos de solución nulos
                );
            }

            // Devolver DTO con información completa del tópico y su solución
            return new DatosTopicoConSolucion(
                    topico.getId(),
                    topico.getTitulo(),
                    topico.getMensaje(),
                    topico.getFechaCreacion(),
                    topico.getStatus().toString(),
                    topico.getAutor(),
                    topico.getCurso().getId(),
                    topico.getCurso().getNombre(),
                    topico.getCurso().getCategoria(),
                    solucion.getId(),
                    solucion.getMensaje(),
                    solucion.getFechaCreacion(),
                    solucion.getAutor()
            );
        });
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================

    /**
     * Valida que no exista un tópico con el mismo título y mensaje.
     *
     * @param titulo Título del tópico
     * @param mensaje Mensaje del tópico
     */
    private void validarDuplicados(String titulo, String mensaje) {
        if (topicoRepository.existsByTituloAndMensaje(titulo, mensaje)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un tópico con ese título y mensaje.");
        }
    }

    /**
     * Valida y convierte el ID del curso de String a Long.
     *
     * @param cursoIdStr ID del curso como cadena
     * @return ID del curso como Long
     */
    private Long validarCursoId(String cursoIdStr) {
        try {
            Long cursoId = Long.parseLong(cursoIdStr.trim());
            if (cursoId <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero positivo.");
            }
            return cursoId;
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cursoId debe ser un número entero válido.");
        }
    }

    /**
     * Valida y convierte el año de String a Integer.
     *
     * @param anoStr Año como cadena
     * @return Año como Integer
     */
    private Integer validarAno(String anoStr) {
        try {
            Integer ano = Integer.parseInt(anoStr.trim());
            if (ano < 1900 || ano > 2100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año debe estar entre 1900 y 2100.");
            }
            return ano;
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'ano' debe ser un número entero válido.");
        }
    }

    /**
     * Obtiene un curso por su ID.
     *
     * @param cursoId ID del curso
     * @return Entidad Curso
     */
    private Curso obtenerCurso(Long cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }

    /**
     * Verifica si existe un curso con el nombre proporcionado (ignorando tildes y mayúsculas).
     *
     * @param nombreCursoBuscado Nombre del curso a buscar
     */
    private void verificarExistenciaCurso(String nombreCursoBuscado) {
        List<Curso> todosCursos = cursoRepository.findAll();
        for (Curso curso : todosCursos) {
            if (normalizarTexto(curso.getNombre()).equals(normalizarTexto(nombreCursoBuscado))) {
                return; // Curso existe, no lanzar excepción
            }
        }
        // Si llegamos aquí, el curso no existe
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nombre del curso no existe.");
    }

    /**
     * Normaliza un texto eliminando tildes y convirtiendo a minúsculas.
     *
     * @param texto Texto a normalizar
     * @return Texto normalizado
     */
    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        return texto.toLowerCase()
                .replaceAll("[áéíóúÁÉÍÓÚ]", "aeiouAEIOU")
                .replaceAll("[àèìòùÀÈÌÒÙ]", "aeiouAEIOU")
                .replaceAll("[äëïöüÄËÏÖÜ]", "aeiouAEIOU");
    }
}