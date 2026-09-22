package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Rol;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {
}
