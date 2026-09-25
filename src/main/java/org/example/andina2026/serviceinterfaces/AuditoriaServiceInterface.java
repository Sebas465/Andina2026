package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Auditoria;

import java.util.List;

public interface AuditoriaServiceInterface {
    public void registrar(String entidad, Long idRegistro, String accion, String detalle);
    public List<Auditoria> historial(String entidad, Long idRegistro);
}
