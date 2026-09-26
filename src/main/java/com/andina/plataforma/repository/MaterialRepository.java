package com.andina.plataforma.repository;

import com.andina.plataforma.model.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    List<Material> findByPersonaIdPersona(Integer idPersona);
}