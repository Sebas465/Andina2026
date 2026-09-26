package com.andina.plataforma.repository;

import com.andina.plataforma.model.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    List<Curso> findAllById(Iterable<Integer> ids);
}