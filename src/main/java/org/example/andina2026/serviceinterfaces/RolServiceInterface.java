package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Rol;

import java.util.List;
import java.util.Optional;

public interface RolServiceInterface {
    public List<Rol> list();
    public void insert(Rol r);
    public Optional<Rol> listId(Long id);
    public void update(Rol r);
    public void delete(Long id);
}
