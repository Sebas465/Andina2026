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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final PersonaServiceInterface personaService;
    private final ModelMapper MM;

    public MatriculaController(MatriculaServiceInterface service, ColegioServiceInterface colegioService, PersonaServiceInterface personaService, ModelMapper MM) {
        this.service = service;
        this.colegioService = colegioService;
        this.personaService = personaService;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<List<MatriculaDTOList>> listar() {
        List<MatriculaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<MatriculaDTOList> buscarPorId(@PathVariable Long id) {
        Matricula e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatriculaDTOList> registrar(@Valid @RequestBody MatriculaDTOInsert dto) {
        Matricula e = MM.map(dto, Matricula.class);
        e.setIdMatricula(null);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody MatriculaDTOInsert dto) {
        buscar(id);
        Matricula e = MM.map(dto, Matricula.class);
        e.setIdMatricula(id);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdMatricula());
        return ResponseEntity.noContent().build();
    }

    private Matricula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Matricula con id: " + id));
    }

    private MatriculaDTOList toList(Matricula e) {
        MatriculaDTOList dto = MM.map(e, MatriculaDTOList.class);
        dto.setIdColegio(e.getColegio() != null ? e.getColegio().getIdColegio() : null);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
