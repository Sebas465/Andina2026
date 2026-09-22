package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.ColegioDTOInsert;
import org.example.andina2026.dtos.ColegioDTOList;
import org.example.andina2026.entities.Colegio;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
public class ColegioController {
    private final ColegioServiceInterface service;
    private final ModelMapper MM;

    public ColegioController(ColegioServiceInterface service, ModelMapper MM) {
        this.service = service;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ColegioDTOList>> listar() {
        List<ColegioDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ColegioDTOList> buscarPorId(@PathVariable Long id) {
        Colegio e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColegioDTOList> registrar(@Valid @RequestBody ColegioDTOInsert dto) {
        Colegio e = MM.map(dto, Colegio.class);
        e.setIdColegio(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdColegio())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColegioDTOList> modificar(@PathVariable Long id, @Valid @RequestBody ColegioDTOInsert dto) {
        buscar(id);
        Colegio e = MM.map(dto, Colegio.class);
        e.setIdColegio(id);
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdColegio());
        return ResponseEntity.noContent().build();
    }

    private Colegio buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + id));
    }

    private ColegioDTOList toList(Colegio e) {
        ColegioDTOList dto = MM.map(e, ColegioDTOList.class);
        return dto;
    }
}
