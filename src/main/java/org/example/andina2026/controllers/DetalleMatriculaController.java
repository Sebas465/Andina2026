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
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;
import org.example.andina2026.entities.Curso;
import org.example.andina2026.entities.Grado;
import org.example.andina2026.entities.Matricula;
import org.example.andina2026.entities.PeriodoAcademico;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/detalles-matricula")
public class DetalleMatriculaController {
    private final DetalleMatriculaServiceInterface service;
    private final CursoServiceInterface cursoService;
    private final GradoServiceInterface gradoService;
    private final MatriculaServiceInterface matriculaService;
    private final PeriodoAcademicoServiceInterface periodoAcademicoService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper modelMapper;

    public DetalleMatriculaController(DetalleMatriculaServiceInterface service, CursoServiceInterface cursoService, GradoServiceInterface gradoService, MatriculaServiceInterface matriculaService, PeriodoAcademicoServiceInterface periodoAcademicoService, AuditoriaServiceInterface auditoria, ModelMapper modelMapper) {
        this.service = service;
        this.cursoService = cursoService;
        this.gradoService = gradoService;
        this.matriculaService = matriculaService;
        this.periodoAcademicoService = periodoAcademicoService;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<DetalleMatriculaDTOList>> listar() {
        List<DetalleMatriculaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<DetalleMatriculaDTOList> buscarPorId(@PathVariable Long id) {
        DetalleMatricula e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<DetalleMatriculaDTOList> registrar(@Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        DetalleMatricula e = modelMapper.map(dto, DetalleMatricula.class);
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
        auditoria.registrar("DetalleMatricula", e.getIdDetalleMatricula(), "CREAR", "Registro creado");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdDetalleMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<DetalleMatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        DetalleMatricula anterior = buscar(id);
        DetalleMatricula e = modelMapper.map(dto, DetalleMatricula.class);
        e.setIdDetalleMatricula(id);
        e.setMatricula(matriculaService.listId(dto.getIdMatricula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Matricula con id: " + dto.getIdMatricula())));
        e.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso())));
        e.setPeriodo(periodoAcademicoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + dto.getIdPeriodo())));
        e.setGrado(gradoService.listId(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + dto.getIdGrado())));
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getFechaMatricula(), e.getFechaMatricula())) cambios.add("fechaMatricula");
        if (!Objects.equals(anterior.getEstado(), e.getEstado())) cambios.add("estado");
        if (!Objects.equals(idDe(anterior.getMatricula()), idDe(e.getMatricula()))) cambios.add("idMatricula");
        if (!Objects.equals(idDe(anterior.getCurso()), idDe(e.getCurso()))) cambios.add("idCurso");
        if (!Objects.equals(idDe(anterior.getPeriodo()), idDe(e.getPeriodo()))) cambios.add("idPeriodo");
        if (!Objects.equals(idDe(anterior.getGrado()), idDe(e.getGrado()))) cambios.add("idGrado");
        service.update(e);
        auditoria.registrar("DetalleMatricula", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdDetalleMatricula());
        auditoria.registrar("DetalleMatricula", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private DetalleMatricula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe DetalleMatricula con id: " + id));
    }

    private static Long idDe(Curso x) {
        return x == null ? null : x.getIdCurso();
    }

    private static Long idDe(Grado x) {
        return x == null ? null : x.getIdGrado();
    }

    private static Long idDe(Matricula x) {
        return x == null ? null : x.getIdMatricula();
    }

    private static Long idDe(PeriodoAcademico x) {
        return x == null ? null : x.getIdPeriodo();
    }

    private DetalleMatriculaDTOList toList(DetalleMatricula e) {
        DetalleMatriculaDTOList dto = modelMapper.map(e, DetalleMatriculaDTOList.class);
        dto.setIdMatricula(e.getMatricula() != null ? e.getMatricula().getIdMatricula() : null);
        dto.setIdCurso(e.getCurso() != null ? e.getCurso().getIdCurso() : null);
        dto.setIdPeriodo(e.getPeriodo() != null ? e.getPeriodo().getIdPeriodo() : null);
        dto.setIdGrado(e.getGrado() != null ? e.getGrado().getIdGrado() : null);
        return dto;
    }
}
