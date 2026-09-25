package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaServiceInterface {
    public List<Matricula> list();
    public void insert(Matricula m);
    public Optional<Matricula> listId(Long id);
    public void update(Matricula m);
    public void delete(Long id);
}
