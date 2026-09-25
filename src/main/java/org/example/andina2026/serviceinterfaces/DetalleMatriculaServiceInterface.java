package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.DetalleMatricula;

import java.util.List;
import java.util.Optional;

public interface DetalleMatriculaServiceInterface {
    public List<DetalleMatricula> list();
    public void insert(DetalleMatricula d);
    public Optional<DetalleMatricula> listId(Long id);
    public void update(DetalleMatricula d);
    public void delete(Long id);
}
