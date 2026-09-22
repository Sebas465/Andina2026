package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Colegio;

import java.util.List;
import java.util.Optional;

public interface ColegioServiceInterface {
    public List<Colegio> list();
    public void insert(Colegio c);
    public Optional<Colegio> listId(Long id);
    public void update(Colegio c);
    public void delete(Long id);
    List<Colegio> escuelasInactivas(java.time.LocalDateTime desde);
    List<Object[]> rendimientoPorColegio(double notaMinima);
    List<Object[]> matriculasPorColegioYPeriodo();
}
