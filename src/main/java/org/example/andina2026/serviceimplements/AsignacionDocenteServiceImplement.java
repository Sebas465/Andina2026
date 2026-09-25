package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.AsignacionDocente;
import org.example.andina2026.repositories.IAsignacionDocenteRepository;
import org.example.andina2026.serviceinterfaces.AsignacionDocenteServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionDocenteServiceImplement implements AsignacionDocenteServiceInterface {
    private final IAsignacionDocenteRepository repository;

    public AsignacionDocenteServiceImplement(IAsignacionDocenteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AsignacionDocente> list() {
        return repository.findAll();
    }

    @Override
    public void insert(AsignacionDocente a) {
        repository.save(a);
    }

    @Override
    public Optional<AsignacionDocente> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(AsignacionDocente a) {
        repository.save(a);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Object[]> cargaDocente() {
        return repository.cargaDocente();
    }
}
