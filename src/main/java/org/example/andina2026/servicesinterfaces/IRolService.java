package org.example.andina2026.servicesinterfaces;

import org.example.andina2026.entities.Rol;

import java.util.List;

public interface IRolService {
    public void insert(Rol r);
    public List<Rol> list();
}
