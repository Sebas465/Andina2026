package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.GradoDTOInsert;
import org.example.andina2026.dtos.GradoDTOList;
import org.example.andina2026.entities.Grado;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.GradoServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/grados")
public class GradoController {
    private final GradoServiceInterface service;
    private final ModelMapper modelMapper;

    public GradoController(GradoServiceInterface service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GradoDTOList>> listar() {
        List<GradoDTOList> lista = service.list()
                .stream()
                .map(e -> modelMapper.map(e, GradoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GradoDTOList> buscarPorId(@PathVariable Long id) {
        Grado e = buscar(id);
        return ResponseEntity.ok(modelMapper.map(e, GradoDTOList.class));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradoDTOList> registrar(@Valid @RequestBody GradoDTOInsert dto) {
        Grado e = modelMapper.map(dto, Grado.class);
        e.setIdGrado(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdGrado())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(e, GradoDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody GradoDTOInsert dto) {
        buscar(id);
        Grado e = modelMapper.map(dto, Grado.class);
        e.setIdGrado(id);
        service.update(e);
        return ResponseEntity.ok(modelMapper.map(e, GradoDTOList.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdGrado());
        return ResponseEntity.noContent().build();
    }

    private Grado buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + id));
    }

}
