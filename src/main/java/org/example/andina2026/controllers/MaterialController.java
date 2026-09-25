package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.MaterialDTOInsert;
import org.example.andina2026.dtos.MaterialDTOList;
import org.example.andina2026.entities.Material;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.MaterialServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/materiales")
public class MaterialController {
    private final MaterialServiceInterface service;
    private final PersonaServiceInterface personaService;
    private final ModelMapper modelMapper;

    public MaterialController(MaterialServiceInterface service, PersonaServiceInterface personaService, ModelMapper modelMapper) {
        this.service = service;
        this.personaService = personaService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MaterialDTOList>> listar() {
        List<MaterialDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialDTOList> buscarPorId(@PathVariable Long id) {
        Material e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MaterialDTOList> registrar(@Valid @RequestBody MaterialDTOInsert dto) {
        Material e = modelMapper.map(dto, Material.class);
        e.setIdMaterial(null);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdMaterial())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MaterialDTOList> modificar(@PathVariable Long id, @Valid @RequestBody MaterialDTOInsert dto) {
        buscar(id);
        Material e = modelMapper.map(dto, Material.class);
        e.setIdMaterial(id);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdMaterial());
        return ResponseEntity.noContent().build();
    }

    private Material buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Material con id: " + id));
    }

    private MaterialDTOList toList(Material e) {
        MaterialDTOList dto = modelMapper.map(e, MaterialDTOList.class);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
