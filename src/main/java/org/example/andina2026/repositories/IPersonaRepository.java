package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Persona;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPersonaRepository extends JpaRepository<Persona, Long> {
    // H2.1: el login busca a la persona por su DNI
    Optional<Persona> findByDni(String dni);


    // ¿A quién debe atender primero psicología? Desaprobados con observación psicológica registrada.
    @Query(value = "SELECT p.*\n" +
            " FROM perfiles_academicos pa\n" +
            " JOIN personas p        ON p.id_persona = pa.id_persona\n" +
            " JOIN roles_persona r   ON r.id_tipo_persona = p.id_tipo_persona\n" +
            " JOIN aulas a           ON a.id_aula = p.id_aula\n" +
            " WHERE UPPER(r.detalle) = 'ALUMNO'\n" +
            "   AND pa.notas < :notaMinima\n" +
            "   AND pa.estado_psicologico IS NOT NULL AND TRIM(pa.estado_psicologico) <> ''\n" +
            " ORDER BY pa.notas ASC", nativeQuery = true)
    List<Persona> alumnosEnRiesgo(@Param("notaMinima") double notaMinima);
}
