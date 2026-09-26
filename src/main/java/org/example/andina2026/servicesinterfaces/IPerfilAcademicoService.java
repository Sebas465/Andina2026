package org.example.andina2026.servicesinterfaces;

import org.example.andina2026.entities.PerfilAcademico;

import java.util.List;

public interface IPerfilAcademicoService {
    public void insert(PerfilAcademico pa);
    public List<PerfilAcademico> list();
}
