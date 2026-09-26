package org.example.andina2026.servicesinterfaces;

import org.example.andina2026.entities.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    public void insert(Rol r);
    public List<Rol> list();
    public Optional<Rol> listId(Long id);
}
