package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Grado;
import org.example.andina2026.repositories.IGradoRepository;
import org.example.andina2026.serviceinterfaces.GradoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class GradoServiceImplement implements GradoServiceInterface {
    private final IGradoRepository repository;

    public GradoServiceImplement(IGradoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Grado> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Grado g) {
        repository.save(g);
    }

    @Override
    public Optional<Grado> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Grado g) {
        repository.save(g);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
//Commit