package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Aula;
import org.example.andina2026.repositories.IAulaRepository;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class AulaServiceImplement implements AulaServiceInterface {
    private final IAulaRepository repository;

    public AulaServiceImplement(IAulaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Aula> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Aula a) {
        repository.save(a);
    }

    @Override
    public Optional<Aula> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Aula a) {
        repository.save(a);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
//Commit