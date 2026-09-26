package org.example.andina2026.servicesimplements;

import org.example.andina2026.entities.Persona;
import org.example.andina2026.repositories.IPersonaRepositories;
import org.example.andina2026.servicesinterfaces.IPersonaService;

import java.util.List;


public class PersonaServicesImplement implements IPersonaService {

    public final IPersonaRepositories pR;

    public PersonaServicesImplement(IPersonaRepositories pR) {
        this.pR = pR;
    }

    @Override
    public void insert(Persona p) {
        pR.save(p);
    }

    @Override
    public List<Persona> list() {
        return pR.findAll();
    }
}
