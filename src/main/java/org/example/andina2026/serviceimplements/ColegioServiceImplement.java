package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Colegio;
import org.example.andina2026.repositories.IColegioRepository;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class ColegioServiceImplement implements ColegioServiceInterface {
    private final IColegioRepository repository;

    public ColegioServiceImplement(IColegioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Colegio> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Colegio c) {
        repository.save(c);
    }

    @Override
    public Optional<Colegio> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Colegio c) {
        repository.save(c);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
