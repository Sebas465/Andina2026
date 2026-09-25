package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Colegio;

import java.util.List;

@Repository
public interface IColegioRepository extends JpaRepository<Colegio, Long> {
    // H1.1: ¿qué escuelas no tienen actividad (cambios registrados o matrículas) desde :desde?
    @Query(value = "SELECT c.id_colegio, c.nombre, c.codigo_modular, MAX(act.fecha) AS ultima\n" +
            " FROM colegios c\n" +
            " LEFT JOIN (\n" +
            "     SELECT m.id_colegio, CAST(dm.fecha_matricula AS TIMESTAMP) AS fecha\n" +
            "     FROM detalles_matricula dm JOIN matriculas m ON m.id_matricula = dm.id_matricula\n" +
            "     UNION ALL\n" +
            "     SELECT a.id_registro, a.fecha FROM auditoria a WHERE a.entidad = 'Colegio'\n" +
            "     UNION ALL\n" +
            "     SELECT au.id_colegio, a.fecha FROM auditoria a JOIN aulas au ON a.entidad = 'Aula' AND a.id_registro = au.id_aula\n" +
            "     UNION ALL\n" +
            "     SELECT au.id_colegio, a.fecha FROM auditoria a\n" +
            "     JOIN personas p ON a.entidad = 'Persona' AND a.id_registro = p.id_persona\n" +
            "     JOIN aulas au ON au.id_aula = p.id_aula\n" +
            " ) act ON act.id_colegio = c.id_colegio\n" +
            " GROUP BY c.id_colegio, c.nombre, c.codigo_modular\n" +
            " HAVING MAX(act.fecha) IS NULL OR MAX(act.fecha) < :desde\n" +
            " ORDER BY ultima ASC NULLS FIRST", nativeQuery = true)
    List<Object[]> escuelasInactivas(@Param("desde") java.time.LocalDateTime desde);

    // ¿Qué colegio necesita más recursos? Promedio y % de desaprobados por colegio.
    @Query(value = "SELECT c.id_colegio, c.nombre, c.tipo_zona,\n" +
            "        COUNT(pa.id_perfil_academico) AS evaluados,\n" +
            "        ROUND(AVG(pa.notas), 2) AS promedio,\n" +
            "        SUM(CASE WHEN pa.notas < :notaMinima THEN 1 ELSE 0 END) AS desaprobados,\n" +
            "        ROUND(100.0 * SUM(CASE WHEN pa.notas < :notaMinima THEN 1 ELSE 0 END)\n" +
            "              / NULLIF(COUNT(pa.id_perfil_academico), 0), 1) AS porcentaje\n" +
            " FROM colegios c\n" +
            " JOIN aulas a                ON a.id_colegio = c.id_colegio\n" +
            " JOIN personas p             ON p.id_aula = a.id_aula\n" +
            " JOIN perfiles_academicos pa ON pa.id_persona = p.id_persona\n" +
            " WHERE pa.notas IS NOT NULL\n" +
            " GROUP BY c.id_colegio, c.nombre, c.tipo_zona\n" +
            " ORDER BY promedio ASC", nativeQuery = true)
    List<Object[]> rendimientoPorColegio(@Param("notaMinima") double notaMinima);

    // ¿Cuánta demanda tiene cada colegio por periodo? Alumnos distintos matriculados.
    @Query(value = "SELECT c.id_colegio, c.nombre, pe.nombre AS periodo, COUNT(DISTINCT m.id_persona) AS alumnos\n" +
            " FROM detalles_matricula dm\n" +
            " JOIN matriculas m           ON m.id_matricula = dm.id_matricula\n" +
            " JOIN colegios c             ON c.id_colegio = m.id_colegio\n" +
            " JOIN periodos_academicos pe ON pe.id_periodo = dm.id_periodo\n" +
            " GROUP BY c.id_colegio, c.nombre, pe.nombre\n" +
            " ORDER BY alumnos DESC", nativeQuery = true)
    List<Object[]> matriculasPorColegioYPeriodo();
}
