package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.MaterialCurso;
import org.example.andina2026.entities.MaterialCursoId;

@Repository
public interface IMaterialCursoRepository extends JpaRepository<MaterialCurso, MaterialCursoId> {
}
