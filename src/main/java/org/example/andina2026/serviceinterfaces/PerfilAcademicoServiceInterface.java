package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.PerfilAcademico;

import java.util.List;
import java.util.Optional;

public interface PerfilAcademicoServiceInterface {
    public List<PerfilAcademico> list();
    public void insert(PerfilAcademico p);
    public Optional<PerfilAcademico> listId(Long id);
    public void update(PerfilAcademico p);
    public void delete(Long id);
    List<Object[]> alumnosConMenorPromedio(int limite, String lengua, Long idGrado);
    List<Object[]> alumnosEnRiesgo(double notaMinima);
}
