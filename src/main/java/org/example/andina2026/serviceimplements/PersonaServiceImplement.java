package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Persona;
import org.example.andina2026.repositories.IPersonaRepository;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImplement implements PersonaServiceInterface {
    private final IPersonaRepository repository;

    public PersonaServiceImplement(IPersonaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Persona> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Persona p) {
        repository.save(p);
    }

    @Override
    public Optional<Persona> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Persona p) {
        repository.save(p);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Persona> alumnosEnRiesgo(double notaMinima) {
        return repository.alumnosEnRiesgo(notaMinima);
    }
}
