package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.PerfilAcademico;
import org.example.andina2026.repositories.IPerfilAcademicoRepository;
import org.example.andina2026.serviceinterfaces.PerfilAcademicoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilAcademicoServiceImplement implements PerfilAcademicoServiceInterface {
    private final IPerfilAcademicoRepository repository;

    public PerfilAcademicoServiceImplement(IPerfilAcademicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PerfilAcademico> list() {
        return repository.findAll();
    }

    @Override
    public void insert(PerfilAcademico p) {
        repository.save(p);
    }

    @Override
    public Optional<PerfilAcademico> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(PerfilAcademico p) {
        repository.save(p);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
//Commit