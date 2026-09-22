package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.CursoDTOInsert;
import org.example.andina2026.dtos.CursoDTOList;
import org.example.andina2026.entities.Curso;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    private final CursoServiceInterface service;
    private final ModelMapper MM;

    public CursoController(CursoServiceInterface service, ModelMapper MM) {
        this.service = service;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CursoDTOList>> listar() {
        List<CursoDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CursoDTOList> buscarPorId(@PathVariable Long id) {
        Curso e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<CursoDTOList> registrar(@Valid @RequestBody CursoDTOInsert dto) {
        Curso e = MM.map(dto, Curso.class);
        e.setIdCurso(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdCurso())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<CursoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody CursoDTOInsert dto) {
        buscar(id);
        Curso e = MM.map(dto, Curso.class);
        e.setIdCurso(id);
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdCurso());
        return ResponseEntity.noContent().build();
    }

    private Curso buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + id));
    }

    private CursoDTOList toList(Curso e) {
        CursoDTOList dto = MM.map(e, CursoDTOList.class);
        return dto;
    }
}
