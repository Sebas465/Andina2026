package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaServiceInterface {
    public List<Persona> list();
    public void insert(Persona p);
    public Optional<Persona> listId(Long id);
    public void update(Persona p);
    public void delete(Long id);
    List<Persona> alumnosEnRiesgo(double notaMinima);
    public Optional<Persona> buscarPorDni(String dni);
}
