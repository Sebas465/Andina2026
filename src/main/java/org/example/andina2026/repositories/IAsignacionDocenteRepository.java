package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.AsignacionDocente;

import java.util.List;

@Repository
public interface IAsignacionDocenteRepository extends JpaRepository<AsignacionDocente, Long> {
    // ¿Algún docente está sobrecargado? Cursos que dicta cada docente por periodo.
    @Query(value = "SELECT p.nombres || ' ' || p.apellidos || ' (' || pe.nombre || ')' AS categoria, COUNT(ad.id_asignacion) AS cantidad\n" +
            " FROM asignaciones_docentes ad\n" +
            " JOIN personas p             ON p.id_persona = ad.id_persona\n" +
            " JOIN periodos_academicos pe ON pe.id_periodo = ad.id_periodo\n" +
            " GROUP BY p.id_persona, p.nombres, p.apellidos, pe.nombre\n" +
            " ORDER BY cantidad DESC, categoria ASC", nativeQuery = true)
    List<Object[]> cargaDocente();
}
