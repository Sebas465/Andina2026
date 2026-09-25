package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.MatriculaDTOInsert;
import org.example.andina2026.dtos.MatriculaDTOList;
import org.example.andina2026.entities.Matricula;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.MatriculaServiceInterface;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;
import org.example.andina2026.entities.Colegio;
import org.example.andina2026.entities.Persona;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final PersonaServiceInterface personaService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper MM;

    public MatriculaController(MatriculaServiceInterface service, ColegioServiceInterface colegioService, PersonaServiceInterface personaService, AuditoriaServiceInterface auditoria, ModelMapper MM) {
        this.service = service;
        this.colegioService = colegioService;
        this.personaService = personaService;
        this.auditoria = auditoria;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<MatriculaDTOList>> listar() {
        List<MatriculaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> buscarPorId(@PathVariable Long id) {
        Matricula e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> registrar(@Valid @RequestBody MatriculaDTOInsert dto) {
        Matricula e = MM.map(dto, Matricula.class);
        e.setIdMatricula(null);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        auditoria.registrar("Matricula", e.getIdMatricula(), "CREAR", "Registro creado");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody MatriculaDTOInsert dto) {
        Matricula anterior = buscar(id);
        Matricula e = MM.map(dto, Matricula.class);
        e.setIdMatricula(id);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(idDe(anterior.getColegio()), idDe(e.getColegio()))) cambios.add("idColegio");
        if (!Objects.equals(idDe(anterior.getPersona()), idDe(e.getPersona()))) cambios.add("idPersona");
        service.update(e);
        auditoria.registrar("Matricula", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdMatricula());
        auditoria.registrar("Matricula", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private Matricula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Matricula con id: " + id));
    }

    private static Long idDe(Colegio x) {
        return x == null ? null : x.getIdColegio();
    }

    private static Long idDe(Persona x) {
        return x == null ? null : x.getIdPersona();
    }


    private MatriculaDTOList toList(Matricula e) {
        MatriculaDTOList dto = MM.map(e, MatriculaDTOList.class);
        dto.setIdColegio(e.getColegio() != null ? e.getColegio().getIdColegio() : null);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
//Commit