package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.AsignacionDocente;

import java.util.List;
import java.util.Optional;

public interface AsignacionDocenteServiceInterface {
    public List<AsignacionDocente> list();
    public void insert(AsignacionDocente a);
    public Optional<AsignacionDocente> listId(Long id);
    public void update(AsignacionDocente a);
    public void delete(Long id);
    List<Object[]> cargaDocente();
}
