package org.example.andina2026.repositories;

import org.example.andina2026.entities.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IColegioRepository extends JpaRepository<Colegio,Long> {

}
