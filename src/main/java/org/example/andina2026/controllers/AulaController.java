package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.AulaDTOInsert;
import org.example.andina2026.dtos.AulaDTOList;
import org.example.andina2026.entities.Aula;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aula")
public class AulaController {
    private final AulaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final ModelMapper MM;

    public AulaController(AulaServiceInterface service, ColegioServiceInterface colegioService, ModelMapper MM) {
        this.service = service;
        this.colegioService = colegioService;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AulaDTOList>> listar() {
        List<AulaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AulaDTOList> buscarPorId(@PathVariable Long id) {
        Aula e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AulaDTOList> registrar(@Valid @RequestBody AulaDTOInsert dto) {
        Aula e = MM.map(dto, Aula.class);
        e.setIdAula(null);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdAula())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AulaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody AulaDTOInsert dto) {
        buscar(id);
        Aula e = MM.map(dto, Aula.class);
        e.setIdAula(id);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdAula());
        return ResponseEntity.noContent().build();
    }

    private Aula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + id));
    }

    private AulaDTOList toList(Aula e) {
        AulaDTOList dto = MM.map(e, AulaDTOList.class);
        dto.setIdColegio(e.getColegio() != null ? e.getColegio().getIdColegio() : null);
        return dto;
    }
}
