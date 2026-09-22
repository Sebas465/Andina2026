package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.MaterialCurso;
import org.example.andina2026.entities.MaterialCursoId;

import java.util.List;
import java.util.Optional;

public interface MaterialCursoServiceInterface {
    public List<MaterialCurso> list();
    public void insert(MaterialCurso m);
    public Optional<MaterialCurso> listId(MaterialCursoId id);
    public void delete(MaterialCursoId id);
}
