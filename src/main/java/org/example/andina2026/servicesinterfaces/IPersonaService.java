package org.example.andina2026.servicesinterfaces;

import org.example.andina2026.entities.Persona;

import java.util.List;

public interface IPersonaService {
    public void insert(Persona p);
    public List<Persona> list();
}
