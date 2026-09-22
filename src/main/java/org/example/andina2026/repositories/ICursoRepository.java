package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Curso;

public interface ICursoRepository extends JpaRepository<Curso,Long> {
}
