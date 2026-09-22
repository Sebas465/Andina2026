package org.example.andina2026.serviceimplements;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Auditoria;
import org.example.andina2026.repositories.IAuditoriaRepository;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaServiceImplement implements AuditoriaServiceInterface {
    private final IAuditoriaRepository repository;

    public AuditoriaServiceImplement(IAuditoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registrar(String entidad, Long idRegistro, String accion, String detalle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Auditoria a = new Auditoria();
        a.setEntidad(entidad);
        a.setIdRegistro(idRegistro);
        a.setAccion(accion);
        a.setUsuario(auth != null ? auth.getName() : "sistema");   // DNI del usuario que hizo el cambio
        a.setFecha(LocalDateTime.now());
        a.setDetalle(detalle);
        repository.save(a);
    }

    @Override
    public List<Auditoria> historial(String entidad, Long idRegistro) {
        if (entidad != null && idRegistro != null) {
            return repository.findByEntidadAndIdRegistroOrderByFechaDesc(entidad, idRegistro);
        }
        if (entidad != null) {
            return repository.findByEntidadOrderByFechaDesc(entidad);
        }
        return repository.findAllByOrderByFechaDesc();
    }
}
