package org.example.andina2026.serviceimplements;

import org.example.andina2026.entities.Colegio;
import org.example.andina2026.repositories.IColegioRepository;
import org.example.andina2026.serviceinterfaces.ColegioSeviceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColegioServiceImplement implements ColegioSeviceInterface {

    public final IColegioRepository ICR;

    public ColegioServiceImplement (IColegioRepository ICR) {
        this.ICR = ICR;
    }
    @Override
    public List<Colegio> list(){return ICR.findAll();}
    @Override
    public void insert(Colegio c){ICR.save(c);}
    @Override
    public Optional<Colegio> listId(Long id){return ICR.findById(id);}
    @Override
    public void delete(Long id){ICR.deleteById(id);}
}
