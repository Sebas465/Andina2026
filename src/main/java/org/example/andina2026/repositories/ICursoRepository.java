package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Curso;

import java.util.List;

@Repository
public interface ICursoRepository extends JpaRepository<Curso, Long> {
    // ¿Qué cursos siguen sin docente en un periodo?
    @Query(value = "SELECT c.*\n" +
            " FROM cursos c\n" +
            " WHERE NOT EXISTS (SELECT 1 FROM asignaciones_docentes ad\n" +
            "                   WHERE ad.id_curso = c.id_curso AND ad.id_periodo = :idPeriodo)\n" +
            " ORDER BY c.nombre", nativeQuery = true)
    List<Curso> cursosSinDocente(@Param("idPeriodo") Long idPeriodo);

    // ¿Para qué cursos hay que preparar material primero?
    @Query(value = "SELECT c.*\n" +
            " FROM cursos c\n" +
            " WHERE NOT EXISTS (SELECT 1 FROM material_curso mc WHERE mc.id_curso = c.id_curso)\n" +
            " ORDER BY c.nombre", nativeQuery = true)
    List<Curso> cursosSinMaterial();
}
