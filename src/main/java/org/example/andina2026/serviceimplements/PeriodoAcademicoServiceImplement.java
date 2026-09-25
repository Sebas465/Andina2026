package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.entities.PeriodoAcademico;
import org.example.andina2026.repositories.IPeriodoAcademicoRepository;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoAcademicoServiceImplement implements PeriodoAcademicoServiceInterface {
    private final IPeriodoAcademicoRepository repository;

    public PeriodoAcademicoServiceImplement(IPeriodoAcademicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PeriodoAcademico> list() {
        return repository.findAll();
    }

    @Override
    public void insert(PeriodoAcademico p) {
        repository.save(p);
    }

    @Override
    public Optional<PeriodoAcademico> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(PeriodoAcademico p) {
        repository.save(p);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
//Commit