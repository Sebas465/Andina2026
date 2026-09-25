package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.PeriodoAcademicoDTOInsert;
import org.example.andina2026.dtos.PeriodoAcademicoDTOList;
import org.example.andina2026.entities.PeriodoAcademico;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoAcademicoController {
    private final PeriodoAcademicoServiceInterface service;
    private final ModelMapper modelMapper;

    public PeriodoAcademicoController(PeriodoAcademicoServiceInterface service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PeriodoAcademicoDTOList>> listar() {
        List<PeriodoAcademicoDTOList> lista = service.list()
                .stream()
                .map(e -> modelMapper.map(e, PeriodoAcademicoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PeriodoAcademicoDTOList> buscarPorId(@PathVariable Long id) {
        PeriodoAcademico e = buscar(id);
        return ResponseEntity.ok(modelMapper.map(e, PeriodoAcademicoDTOList.class));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<PeriodoAcademicoDTOList> registrar(@Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        PeriodoAcademico e = modelMapper.map(dto, PeriodoAcademico.class);
        e.setIdPeriodo(null);
        validar(e, null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdPeriodo())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(e, PeriodoAcademicoDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<PeriodoAcademicoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        buscar(id);
        PeriodoAcademico e = modelMapper.map(dto, PeriodoAcademico.class);
        e.setIdPeriodo(id);
        validar(e, id);
        service.update(e);
        return ResponseEntity.ok(modelMapper.map(e, PeriodoAcademicoDTOList.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdPeriodo());
        return ResponseEntity.noContent().build();
    }

    private PeriodoAcademico buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + id));
    }
    private void validar(PeriodoAcademico e, Long id) {
        if (e.getFechaFin().isBefore(e.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

}
