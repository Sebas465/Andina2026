package org.example.andina2026.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.andina2026.entities.Aula;

@Repository
public interface IAulaRepository extends JpaRepository<Aula, Long> {
}
//Commit