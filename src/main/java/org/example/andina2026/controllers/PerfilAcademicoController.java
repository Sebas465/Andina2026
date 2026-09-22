package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.PerfilAcademicoDTOInsert;
import org.example.andina2026.dtos.PerfilAcademicoDTOList;
import org.example.andina2026.entities.PerfilAcademico;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PerfilAcademicoServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/perfiles-academicos")
public class PerfilAcademicoController {
    private final PerfilAcademicoServiceInterface service;
    private final PersonaServiceInterface personaService;
    private final ModelMapper MM;

    public PerfilAcademicoController(PerfilAcademicoServiceInterface service, PersonaServiceInterface personaService, ModelMapper MM) {
        this.service = service;
        this.personaService = personaService;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO','DOCENTE')")
    public ResponseEntity<List<PerfilAcademicoDTOList>> listar() {
        List<PerfilAcademicoDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO')")
    public ResponseEntity<PerfilAcademicoDTOInsert> buscarPorId(@PathVariable Long id) {
        PerfilAcademico e = buscar(id);
        return ResponseEntity.ok(toDetail(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO')")
    public ResponseEntity<PerfilAcademicoDTOInsert> registrar(@Valid @RequestBody PerfilAcademicoDTOInsert dto) {
        PerfilAcademico e = MM.map(dto, PerfilAcademico.class);
        e.setIdPerfilAcademico(null);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdPerfilAcademico())
                .toUri();
        return ResponseEntity.created(location).body(toDetail(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO')")
    public ResponseEntity<PerfilAcademicoDTOInsert> modificar(@PathVariable Long id, @Valid @RequestBody PerfilAcademicoDTOInsert dto) {
        buscar(id);
        PerfilAcademico e = MM.map(dto, PerfilAcademico.class);
        e.setIdPerfilAcademico(id);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.update(e);
        return ResponseEntity.ok(toDetail(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdPerfilAcademico());
        return ResponseEntity.noContent().build();
    }

    private PerfilAcademico buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PerfilAcademico con id: " + id));
    }

    private PerfilAcademicoDTOList toList(PerfilAcademico e) {
        PerfilAcademicoDTOList dto = MM.map(e, PerfilAcademicoDTOList.class);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }

    private PerfilAcademicoDTOInsert toDetail(PerfilAcademico e) {
        PerfilAcademicoDTOInsert dto = MM.map(e, PerfilAcademicoDTOInsert.class);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
