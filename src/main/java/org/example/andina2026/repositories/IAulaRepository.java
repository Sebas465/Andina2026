package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Aula;

import java.util.List;

@Repository
public interface IAulaRepository extends JpaRepository<Aula, Long> {
    // ¿Hay que abrir secciones o redistribuir? Alumnos por aula.
    @Query(value = "SELECT a.nombre || ' - ' || c.nombre AS categoria, COUNT(p.id_persona) AS cantidad\n" +
            " FROM aulas a\n" +
            " JOIN colegios c ON c.id_colegio = a.id_colegio\n" +
            " LEFT JOIN personas p ON p.id_aula = a.id_aula\n" +
            "      AND p.id_tipo_persona IN (SELECT id_tipo_persona FROM roles_persona WHERE UPPER(detalle) = 'ALUMNO')\n" +
            " GROUP BY a.id_aula, a.nombre, c.nombre\n" +
            " ORDER BY cantidad DESC, categoria ASC", nativeQuery = true)
    List<Object[]> ocupacionDeAulas();
}
