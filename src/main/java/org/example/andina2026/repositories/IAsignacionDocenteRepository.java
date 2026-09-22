package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;

import java.util.List;

public interface IAsignacionDocenteRepository extends JpaRepository<AsignacionDocente,Long> {
public List<AsignacionDTOList> findByHorassemanalesIsBetween(Long hora_min, Long hora_max);
}
