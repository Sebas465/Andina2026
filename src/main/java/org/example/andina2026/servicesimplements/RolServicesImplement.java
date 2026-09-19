package org.example.andina2026.servicesimplements;

import org.example.andina2026.entities.Rol;
import org.example.andina2026.servicesinterfaces.IRolService;

import java.util.List;

public class RolServicesImplement implements IRolService {

    public final IRolService rS;

    public RolServicesImplement(IRolService rS) {
        this.rS = rS;
    }

    @Override
    public void insert(Rol r) {

    }

    @Override
    public List<Rol> list() {
        return List.of();
    }
}
