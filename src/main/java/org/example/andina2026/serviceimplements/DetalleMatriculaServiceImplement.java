package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.DetalleMatricula;
import org.example.andina2026.repositories.IDetalleMatriculaRepository;
import org.example.andina2026.serviceinterfaces.DetalleMatriculaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleMatriculaServiceImplement implements DetalleMatriculaServiceInterface {
    private final IDetalleMatriculaRepository repository;

    public DetalleMatriculaServiceImplement(IDetalleMatriculaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DetalleMatricula> list() {
        return repository.findAll();
    }

    @Override
    public void insert(DetalleMatricula d) {
        repository.save(d);
    }

    @Override
    public Optional<DetalleMatricula> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(DetalleMatricula d) {
        repository.save(d);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
