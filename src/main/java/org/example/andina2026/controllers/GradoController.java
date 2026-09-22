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
    private final ModelMapper MM;

    public GradoController(GradoServiceInterface service, ModelMapper MM) {
        this.service = service;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GradoDTOList>> listar() {
        List<GradoDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GradoDTOList> buscarPorId(@PathVariable Long id) {
        Grado e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradoDTOList> registrar(@Valid @RequestBody GradoDTOInsert dto) {
        Grado e = MM.map(dto, Grado.class);
        e.setIdGrado(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdGrado())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody GradoDTOInsert dto) {
        buscar(id);
        Grado e = MM.map(dto, Grado.class);
        e.setIdGrado(id);
        service.update(e);
        return ResponseEntity.ok(toList(e));
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

    private GradoDTOList toList(Grado e) {
        GradoDTOList dto = MM.map(e, GradoDTOList.class);
        return dto;
    }
}
