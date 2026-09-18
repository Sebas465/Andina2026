package org.example.andina2026.repositories;

import org.example.andina2026.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAulaRepository extends JpaRepository<Aula,Long> {
}
