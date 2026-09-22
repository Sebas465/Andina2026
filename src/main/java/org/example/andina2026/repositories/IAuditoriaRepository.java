package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Auditoria;

import java.util.List;

@Repository
public interface IAuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByEntidadAndIdRegistroOrderByFechaDesc(String entidad, Long idRegistro);

    List<Auditoria> findByEntidadOrderByFechaDesc(String entidad);

    List<Auditoria> findAllByOrderByFechaDesc();
}
