package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Curso;
import org.example.andina2026.repositories.ICursoRepository;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImplement implements CursoServiceInterface {
    private final ICursoRepository repository;

    public CursoServiceImplement(ICursoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Curso> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Curso c) {
        repository.save(c);
    }

    @Override
    public Optional<Curso> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Curso c) {
        repository.save(c);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Object[]> cursosSinDocente(Long idPeriodo) {
        return repository.cursosSinDocente(idPeriodo);
    }

    @Override
    public List<Object[]> cursosSinMaterial() {
        return repository.cursosSinMaterial();
    }
}
