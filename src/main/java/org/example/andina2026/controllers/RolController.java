package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.RolDTOInsert;
import org.example.andina2026.dtos.RolDTOList;
import org.example.andina2026.entities.Rol;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.RolServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/roles-persona")
public class RolController {
    private final RolServiceInterface service;
    private final ModelMapper modelMapper;

    public RolController(RolServiceInterface service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RolDTOList>> listar() {
        List<RolDTOList> lista = service.list()
                .stream()
                .map(e -> modelMapper.map(e, RolDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolDTOList> buscarPorId(@PathVariable Long id) {
        Rol e = buscar(id);
        return ResponseEntity.ok(modelMapper.map(e, RolDTOList.class));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolDTOList> registrar(@Valid @RequestBody RolDTOInsert dto) {
        Rol e = modelMapper.map(dto, Rol.class);
        e.setIdTipoPersona(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdTipoPersona())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(e, RolDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolDTOList> modificar(@PathVariable Long id, @Valid @RequestBody RolDTOInsert dto) {
        buscar(id);
        Rol e = modelMapper.map(dto, Rol.class);
        e.setIdTipoPersona(id);
        service.update(e);
        return ResponseEntity.ok(modelMapper.map(e, RolDTOList.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdTipoPersona());
        return ResponseEntity.noContent().build();
    }

    private Rol buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + id));
    }

}
