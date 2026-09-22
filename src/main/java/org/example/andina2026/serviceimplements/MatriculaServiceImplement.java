package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Matricula;
import org.example.andina2026.repositories.IMatriculaRepository;
import org.example.andina2026.serviceinterfaces.MatriculaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculaServiceImplement implements MatriculaServiceInterface {
    private final IMatriculaRepository repository;

    public MatriculaServiceImplement(IMatriculaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Matricula> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Matricula m) {
        repository.save(m);
    }

    @Override
    public Optional<Matricula> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Matricula m) {
        repository.save(m);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
