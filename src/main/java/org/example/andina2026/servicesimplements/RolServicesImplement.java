package org.example.andina2026.servicesimplements;

import org.example.andina2026.entities.Rol;
import org.example.andina2026.repositories.IRolRepository;
import org.example.andina2026.servicesinterfaces.IRolService;

import java.util.List;

public class RolServicesImplement implements IRolService {

    public final IRolRepository rR;

    public RolServicesImplement(IRolRepository rR) {
        this.rR = rR;
    }

    @Override
    public void insert(Rol r) {
        rR.save(r);
    }

    @Override
    public List<Rol> list() {
        return rR.findAll();
    }
}
