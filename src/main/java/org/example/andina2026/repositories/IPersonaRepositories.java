package org.example.andina2026.repositories;

import org.example.andina2026.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPersonaRepositories extends JpaRepository<Persona, Long> {
}
