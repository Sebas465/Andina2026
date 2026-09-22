package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Grado;

import java.util.List;
import java.util.Optional;

public interface GradoServiceInterface {
    public List<Grado> list();
    public void insert(Grado g);
    public Optional<Grado> listId(Long id);
    public void update(Grado g);
    public void delete(Long id);
}
