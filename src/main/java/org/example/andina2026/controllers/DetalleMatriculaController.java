package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.DetalleMatriculaDTOInsert;
import org.example.andina2026.dtos.DetalleMatriculaDTOList;
import org.example.andina2026.entities.DetalleMatricula;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.DetalleMatriculaServiceInterface;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;
import org.example.andina2026.serviceinterfaces.GradoServiceInterface;
import org.example.andina2026.serviceinterfaces.MatriculaServiceInterface;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-matricula")
public class DetalleMatriculaController {
    private final DetalleMatriculaServiceInterface service;
    private final CursoServiceInterface cursoService;
    private final GradoServiceInterface gradoService;
    private final MatriculaServiceInterface matriculaService;
    private final PeriodoAcademicoServiceInterface periodoAcademicoService;
    private final ModelMapper MM;

    public DetalleMatriculaController(DetalleMatriculaServiceInterface service, CursoServiceInterface cursoService, GradoServiceInterface gradoService, MatriculaServiceInterface matriculaService, PeriodoAcademicoServiceInterface periodoAcademicoService, ModelMapper MM) {
        this.service = service;
        this.cursoService = cursoService;
        this.gradoService = gradoService;
        this.matriculaService = matriculaService;
        this.periodoAcademicoService = periodoAcademicoService;
        this.MM = MM;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<List<DetalleMatriculaDTOList>> listar() {
        List<DetalleMatriculaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<DetalleMatriculaDTOList> buscarPorId(@PathVariable Long id) {
        DetalleMatricula e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetalleMatriculaDTOList> registrar(@Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        DetalleMatricula e = MM.map(dto, DetalleMatricula.class);
        e.setIdDetalleMatricula(null);
        e.setMatricula(matriculaService.listId(dto.getIdMatricula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Matricula con id: " + dto.getIdMatricula())));
        e.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso())));
        e.setPeriodo(periodoAcademicoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + dto.getIdPeriodo())));
        e.setGrado(gradoService.listId(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + dto.getIdGrado())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdDetalleMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetalleMatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        buscar(id);
        DetalleMatricula e = MM.map(dto, DetalleMatricula.class);
        e.setIdDetalleMatricula(id);
        e.setMatricula(matriculaService.listId(dto.getIdMatricula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Matricula con id: " + dto.getIdMatricula())));
        e.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso())));
        e.setPeriodo(periodoAcademicoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + dto.getIdPeriodo())));
        e.setGrado(gradoService.listId(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + dto.getIdGrado())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdDetalleMatricula());
        return ResponseEntity.noContent().build();
    }

    private DetalleMatricula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe DetalleMatricula con id: " + id));
    }

    private DetalleMatriculaDTOList toList(DetalleMatricula e) {
        DetalleMatriculaDTOList dto = MM.map(e, DetalleMatriculaDTOList.class);
        dto.setIdMatricula(e.getMatricula() != null ? e.getMatricula().getIdMatricula() : null);
        dto.setIdCurso(e.getCurso() != null ? e.getCurso().getIdCurso() : null);
        dto.setIdPeriodo(e.getPeriodo() != null ? e.getPeriodo().getIdPeriodo() : null);
        dto.setIdGrado(e.getGrado() != null ? e.getGrado().getIdGrado() : null);
        return dto;
    }
}
