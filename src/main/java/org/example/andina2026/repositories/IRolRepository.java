package org.example.andina2026.repositories;

import org.example.andina2026.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {
    public List<Rol> findByIdRol(Long id);

}
