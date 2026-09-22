package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.MaterialCurso;
import org.example.andina2026.entities.MaterialCursoId;
import org.example.andina2026.repositories.IMaterialCursoRepository;
import org.example.andina2026.serviceinterfaces.MaterialCursoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialCursoServiceImplement implements MaterialCursoServiceInterface {
    private final IMaterialCursoRepository repository;

    public MaterialCursoServiceImplement(IMaterialCursoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaterialCurso> list() {
        return repository.findAll();
    }

    @Override
    public void insert(MaterialCurso m) {
        repository.save(m);
    }

    @Override
    public Optional<MaterialCurso> listId(MaterialCursoId id) {
        return repository.findById(id);
    }

    @Override
    public void delete(MaterialCursoId id) {
        repository.deleteById(id);
    }
}
