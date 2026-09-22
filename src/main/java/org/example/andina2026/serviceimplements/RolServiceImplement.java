package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Rol;
import org.example.andina2026.repositories.IRolRepository;
import org.example.andina2026.serviceinterfaces.RolServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImplement implements RolServiceInterface {
    private final IRolRepository repository;

    public RolServiceImplement(IRolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Rol r) {
        repository.save(r);
    }

    @Override
    public Optional<Rol> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Rol r) {
        repository.save(r);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
