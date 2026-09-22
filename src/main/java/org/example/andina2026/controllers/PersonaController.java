package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.PersonaDTOInsert;
import org.example.andina2026.dtos.PersonaDTOList;
import org.example.andina2026.entities.Persona;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.RolServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    private final PersonaServiceInterface service;
    private final AulaServiceInterface aulaService;
    private final RolServiceInterface rolService;
    private final ModelMapper MM;

    public PersonaController(PersonaServiceInterface service, AulaServiceInterface aulaService, RolServiceInterface rolService, ModelMapper MM) {
        this.service = service;
        this.aulaService = aulaService;
        this.rolService = rolService;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','PSICOLOGO')")
    public ResponseEntity<List<PersonaDTOList>> listar() {
        List<PersonaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonaDTOInsert> buscarPorId(@PathVariable Long id) {
        Persona e = buscar(id);
        return ResponseEntity.ok(toDetail(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonaDTOInsert> registrar(@Valid @RequestBody PersonaDTOInsert dto) {
        Persona e = MM.map(dto, Persona.class);
        e.setIdPersona(null);
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdPersona())
                .toUri();
        return ResponseEntity.created(location).body(toDetail(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonaDTOInsert> modificar(@PathVariable Long id, @Valid @RequestBody PersonaDTOInsert dto) {
        buscar(id);
        Persona e = MM.map(dto, Persona.class);
        e.setIdPersona(id);
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        service.update(e);
        return ResponseEntity.ok(toDetail(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdPersona());
        return ResponseEntity.noContent().build();
    }

    private Persona buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + id));
    }

    private PersonaDTOList toList(Persona e) {
        PersonaDTOList dto = MM.map(e, PersonaDTOList.class);
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdRol(e.getRol() != null ? e.getRol().getIdTipoPersona() : null);
        return dto;
    }

    private PersonaDTOInsert toDetail(Persona e) {
        PersonaDTOInsert dto = MM.map(e, PersonaDTOInsert.class);
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdRol(e.getRol() != null ? e.getRol().getIdTipoPersona() : null);
        return dto;
    }
}
