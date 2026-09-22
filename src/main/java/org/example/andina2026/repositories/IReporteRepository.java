package org.example.andina2026.repositories;

import org.example.andina2026.entities.PerfilAcademico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Consultas nativas para TOMA DE DECISIONES (como obtenerTotalPeliculasPorStreaming de demoSM2).
 * Alumno = persona cuyo tipo (tabla Rol del ERD) es 'ALUMNO'; docente = tipo 'DOCENTE'.
 * Notas = promedio vigesimal 0-20; se desaprueba con menos de 11.
 */
public interface IReporteRepository extends Repository<PerfilAcademico, Long> {

    /** ¿A quién apoyar primero? Alumnos con el promedio más bajo. */
    @Query(value = """
            SELECT p.id_persona, p.nombres, p.apellidos, a.nombre AS aula, c.nombre AS colegio, pa.notas
            FROM perfiles_academicos pa
            JOIN personas p        ON p.id_persona = pa.id_persona
            JOIN roles_persona r   ON r.id_tipo_persona = p.id_tipo_persona
            JOIN aulas a           ON a.id_aula = p.id_aula
            JOIN colegios c        ON c.id_colegio = a.id_colegio
            WHERE UPPER(r.detalle) = 'ALUMNO' AND pa.notas IS NOT NULL
              AND (CAST(:lengua AS VARCHAR) IS NULL OR p.lengua_materna = CAST(:lengua AS VARCHAR))
              AND (CAST(:idGrado AS BIGINT) IS NULL OR EXISTS (
                    SELECT 1 FROM matriculas m JOIN detalles_matricula dm ON dm.id_matricula = m.id_matricula
                    WHERE m.id_persona = p.id_persona AND dm.id_grado = CAST(:idGrado AS BIGINT)))
            ORDER BY pa.notas ASC, p.apellidos ASC
            LIMIT :limite
            """, nativeQuery = true)
    List<Object[]> alumnosConMenorPromedio(@Param("limite") int limite, @Param("lengua") String lengua,
                                           @Param("idGrado") Long idGrado);

    /** H1.1: ¿qué escuelas no tienen actividad (cambios registrados o matrículas) desde :desde? */
    @Query(value = """
            SELECT c.id_colegio, c.nombre, c.codigo_modular, MAX(act.fecha) AS ultima
            FROM colegios c
            LEFT JOIN (
                SELECT m.id_colegio, CAST(dm.fecha_matricula AS TIMESTAMP) AS fecha
                FROM detalles_matricula dm JOIN matriculas m ON m.id_matricula = dm.id_matricula
                UNION ALL
                SELECT a.id_registro, a.fecha FROM auditoria a WHERE a.entidad = 'Colegio'
                UNION ALL
                SELECT au.id_colegio, a.fecha FROM auditoria a JOIN aulas au ON a.entidad = 'Aula' AND a.id_registro = au.id_aula
                UNION ALL
                SELECT au.id_colegio, a.fecha FROM auditoria a
                JOIN personas p ON a.entidad = 'Persona' AND a.id_registro = p.id_persona
                JOIN aulas au ON au.id_aula = p.id_aula
            ) act ON act.id_colegio = c.id_colegio
            GROUP BY c.id_colegio, c.nombre, c.codigo_modular
            HAVING MAX(act.fecha) IS NULL OR MAX(act.fecha) < :desde
            ORDER BY ultima ASC NULLS FIRST
            """, nativeQuery = true)
    List<Object[]> escuelasInactivas(@Param("desde") java.time.LocalDateTime desde);

    /** ¿A quién debe atender primero psicología? Desaprobados con observación psicológica registrada. */
    @Query(value = """
            SELECT p.id_persona, p.nombres, p.apellidos, a.nombre AS aula, pa.notas
            FROM perfiles_academicos pa
            JOIN personas p        ON p.id_persona = pa.id_persona
            JOIN roles_persona r   ON r.id_tipo_persona = p.id_tipo_persona
            JOIN aulas a           ON a.id_aula = p.id_aula
            WHERE UPPER(r.detalle) = 'ALUMNO'
              AND pa.notas < :notaMinima
              AND pa.estado_psicologico IS NOT NULL AND TRIM(pa.estado_psicologico) <> ''
            ORDER BY pa.notas ASC
            """, nativeQuery = true)
    List<Object[]> alumnosEnRiesgo(@Param("notaMinima") double notaMinima);

    /** ¿Qué colegio necesita más recursos? Promedio y % de desaprobados por colegio. */
    @Query(value = """
            SELECT c.id_colegio, c.nombre, c.tipo_zona,
                   COUNT(pa.id_perfil_academico) AS evaluados,
                   ROUND(AVG(pa.notas), 2) AS promedio,
                   SUM(CASE WHEN pa.notas < :notaMinima THEN 1 ELSE 0 END) AS desaprobados,
                   ROUND(100.0 * SUM(CASE WHEN pa.notas < :notaMinima THEN 1 ELSE 0 END)
                         / NULLIF(COUNT(pa.id_perfil_academico), 0), 1) AS porcentaje
            FROM colegios c
            JOIN aulas a                ON a.id_colegio = c.id_colegio
            JOIN personas p             ON p.id_aula = a.id_aula
            JOIN perfiles_academicos pa ON pa.id_persona = p.id_persona
            WHERE pa.notas IS NOT NULL
            GROUP BY c.id_colegio, c.nombre, c.tipo_zona
            ORDER BY promedio ASC
            """, nativeQuery = true)
    List<Object[]> rendimientoPorColegio(@Param("notaMinima") double notaMinima);

    /** ¿Hay que abrir secciones o redistribuir? Alumnos asignados vs capacidad de cada aula. */
    @Query(value = """
            SELECT a.id_aula, a.nombre, a.seccion, c.nombre AS colegio, a.capacidad,
                   COUNT(p.id_persona) AS alumnos,
                   ROUND(100.0 * COUNT(p.id_persona) / NULLIF(a.capacidad, 0), 1) AS ocupacion
            FROM aulas a
            JOIN colegios c ON c.id_colegio = a.id_colegio
            LEFT JOIN personas p ON p.id_aula = a.id_aula
                 AND p.id_tipo_persona IN (SELECT id_tipo_persona FROM roles_persona WHERE UPPER(detalle) = 'ALUMNO')
            GROUP BY a.id_aula, a.nombre, a.seccion, c.nombre, a.capacidad
            ORDER BY ocupacion DESC
            """, nativeQuery = true)
    List<Object[]> ocupacionDeAulas();

    /** ¿Algún docente está sobrecargado? Cursos y horas semanales por docente y periodo. */
    @Query(value = """
            SELECT p.id_persona, p.nombres, p.apellidos, pe.nombre AS periodo,
                   COUNT(ad.id_asignacion) AS cursos, COALESCE(SUM(ad.horas_semanales), 0) AS horas
            FROM asignaciones_docentes ad
            JOIN personas p            ON p.id_persona = ad.id_persona
            JOIN periodos_academicos pe ON pe.id_periodo = ad.id_periodo
            GROUP BY p.id_persona, p.nombres, p.apellidos, pe.nombre
            ORDER BY horas DESC
            """, nativeQuery = true)
    List<Object[]> cargaDocente();

    /** ¿Qué cursos siguen sin docente en un periodo? */
    @Query(value = """
            SELECT c.id_curso, c.nombre, c.area
            FROM cursos c
            WHERE NOT EXISTS (SELECT 1 FROM asignaciones_docentes ad
                              WHERE ad.id_curso = c.id_curso AND ad.id_periodo = :idPeriodo)
            ORDER BY c.nombre
            """, nativeQuery = true)
    List<Object[]> cursosSinDocente(@Param("idPeriodo") Long idPeriodo);

    /** ¿Para qué cursos hay que preparar material primero? */
    @Query(value = """
            SELECT c.id_curso, c.nombre, c.area
            FROM cursos c
            WHERE NOT EXISTS (SELECT 1 FROM material_curso mc WHERE mc.id_curso = c.id_curso)
            ORDER BY c.nombre
            """, nativeQuery = true)
    List<Object[]> cursosSinMaterial();

    /** ¿Cuánta demanda tiene cada colegio por periodo? Alumnos distintos matriculados. */
    @Query(value = """
            SELECT c.id_colegio, c.nombre, pe.nombre AS periodo, COUNT(DISTINCT m.id_persona) AS alumnos
            FROM detalles_matricula dm
            JOIN matriculas m           ON m.id_matricula = dm.id_matricula
            JOIN colegios c             ON c.id_colegio = m.id_colegio
            JOIN periodos_academicos pe ON pe.id_periodo = dm.id_periodo
            GROUP BY c.id_colegio, c.nombre, pe.nombre
            ORDER BY alumnos DESC
            """, nativeQuery = true)
    List<Object[]> matriculasPorColegioYPeriodo();
}
