package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Aula;

import java.util.List;
import java.util.Optional;

public interface AulaServiceInterface {
    public List<Aula> list();
    public void insert(Aula a);
    public Optional<Aula> listId(Long id);
    public void update(Aula a);
    public void delete(Long id);
}
