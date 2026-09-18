package org.example.andina2026.serviceimplements;

import org.example.andina2026.entities.Aula;
import org.example.andina2026.repositories.IAulaRepository;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;

import java.util.List;
import java.util.Optional;

public class AulaServiceImplement implements AulaServiceInterface {

    private final IAulaRepository IAR;
    public AulaServiceImplement(IAulaRepository iar){IAR =iar;}

    @Override
    public List<Aula> list(){return IAR.findAll();}

    @Override
    public void insert(Aula a){IAR.save(a);}

    @Override
    public Optional<Aula> listId(Long id){return IAR.findById(id);}

    @Override
    public void delete(Long id){IAR.deleteById(id);}
}
