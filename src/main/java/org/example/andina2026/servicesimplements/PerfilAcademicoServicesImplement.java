package org.example.andina2026.servicesimplements;

import org.example.andina2026.entities.PerfilAcademico;
import org.example.andina2026.repositories.IPerfilAcademicoRepositories;
import org.example.andina2026.servicesinterfaces.IPerfilAcademicoService;

import java.util.List;

public class PerfilAcademicoServicesImplement implements IPerfilAcademicoService {

    public final IPerfilAcademicoRepositories paR;

    public PerfilAcademicoServicesImplement(IPerfilAcademicoRepositories paR) {
        this.paR = paR;
    }

    @Override
    public void insert(PerfilAcademico pa) {
        paR.save(pa);
    }

    @Override
    public List<PerfilAcademico> list() {
        return paR.findAll();
    }
}
