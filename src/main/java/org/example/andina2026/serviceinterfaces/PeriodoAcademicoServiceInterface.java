package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.PeriodoAcademico;

import java.util.List;
import java.util.Optional;

public interface PeriodoAcademicoServiceInterface {
    public List<PeriodoAcademico> list();
    public void insert(PeriodoAcademico p);
    public Optional<PeriodoAcademico> listId(Long id);
    public void update(PeriodoAcademico p);
    public void delete(Long id);
}
//Commit