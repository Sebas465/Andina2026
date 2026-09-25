package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoServiceInterface {
    public List<Curso> list();
    public void insert(Curso c);
    public Optional<Curso> listId(Long id);
    public void update(Curso c);
    public void delete(Long id);
}
//Commit