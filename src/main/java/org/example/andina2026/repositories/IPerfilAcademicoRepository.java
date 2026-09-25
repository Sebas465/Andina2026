package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.PerfilAcademico;

import java.util.List;

@Repository
public interface IPerfilAcademicoRepository extends JpaRepository<PerfilAcademico, Long> {
    // Consultas nativas para tomar decisiones. Notas = promedio vigesimal 0-20; se desaprueba con menos de 11.
    // ¿A quién apoyar primero? Alumnos con el promedio más bajo (filtros opcionales: lengua y grado).
    @Query(value = "SELECT p.id_persona, p.nombres, p.apellidos, a.nombre AS aula, c.nombre AS colegio, pa.notas\n" +
            " FROM perfiles_academicos pa\n" +
            " JOIN personas p        ON p.id_persona = pa.id_persona\n" +
            " JOIN roles_persona r   ON r.id_tipo_persona = p.id_tipo_persona\n" +
            " JOIN aulas a           ON a.id_aula = p.id_aula\n" +
            " JOIN colegios c        ON c.id_colegio = a.id_colegio\n" +
            " WHERE UPPER(r.detalle) = 'ALUMNO' AND pa.notas IS NOT NULL\n" +
            "   AND (CAST(:lengua AS VARCHAR) IS NULL OR p.lengua_materna = CAST(:lengua AS VARCHAR))\n" +
            "   AND (CAST(:idGrado AS BIGINT) IS NULL OR EXISTS (\n" +
            "         SELECT 1 FROM matriculas m JOIN detalles_matricula dm ON dm.id_matricula = m.id_matricula\n" +
            "         WHERE m.id_persona = p.id_persona AND dm.id_grado = CAST(:idGrado AS BIGINT)))\n" +
            " ORDER BY pa.notas ASC, p.apellidos ASC\n" +
            " LIMIT :limite", nativeQuery = true)
    List<Object[]> alumnosConMenorPromedio(@Param("limite") int limite, @Param("lengua") String lengua, @Param("idGrado") Long idGrado);

    // ¿A quién debe atender primero psicología? Desaprobados con observación psicológica registrada.
    @Query(value = "SELECT p.id_persona, p.nombres, p.apellidos, a.nombre AS aula, pa.notas\n" +
            " FROM perfiles_academicos pa\n" +
            " JOIN personas p        ON p.id_persona = pa.id_persona\n" +
            " JOIN roles_persona r   ON r.id_tipo_persona = p.id_tipo_persona\n" +
            " JOIN aulas a           ON a.id_aula = p.id_aula\n" +
            " WHERE UPPER(r.detalle) = 'ALUMNO'\n" +
            "   AND pa.notas < :notaMinima\n" +
            "   AND pa.estado_psicologico IS NOT NULL AND TRIM(pa.estado_psicologico) <> ''\n" +
            " ORDER BY pa.notas ASC", nativeQuery = true)
    List<Object[]> alumnosEnRiesgo(@Param("notaMinima") double notaMinima);
}
