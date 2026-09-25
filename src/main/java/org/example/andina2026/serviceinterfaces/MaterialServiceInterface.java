package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Material;

import java.util.List;
import java.util.Optional;

public interface MaterialServiceInterface {
    public List<Material> list();
    public void insert(Material m);
    public Optional<Material> listId(Long id);
    public void update(Material m);
    public void delete(Long id);
}
//Commit