package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Material;
import org.example.andina2026.repositories.IMaterialRepository;
import org.example.andina2026.serviceinterfaces.MaterialServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImplement implements MaterialServiceInterface {
    private final IMaterialRepository repository;

    public MaterialServiceImplement(IMaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Material> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Material m) {
        repository.save(m);
    }

    @Override
    public Optional<Material> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Material m) {
        repository.save(m);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
