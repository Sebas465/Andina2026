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
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;
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
    private final PeriodoAcademicoServiceInterface periodoService;
    private final ModelMapper modelMapper;

    public CursoController(CursoServiceInterface service, PeriodoAcademicoServiceInterface periodoService, ModelMapper modelMapper) {
        this.service = service;
        this.periodoService = periodoService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CursoDTOList>> listar() {
        List<CursoDTOList> lista = service.list()
                .stream()
                .map(e -> modelMapper.map(e, CursoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CursoDTOList> buscarPorId(@PathVariable Long id) {
        Curso e = buscar(id);
        return ResponseEntity.ok(modelMapper.map(e, CursoDTOList.class));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<CursoDTOList> registrar(@Valid @RequestBody CursoDTOInsert dto) {
        Curso e = modelMapper.map(dto, Curso.class);
        e.setIdCurso(null);
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdCurso())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(e, CursoDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<CursoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody CursoDTOInsert dto) {
        buscar(id);
        Curso e = modelMapper.map(dto, Curso.class);
        e.setIdCurso(id);
        service.update(e);
        return ResponseEntity.ok(modelMapper.map(e, CursoDTOList.class));
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


    // ---------------------------------------------------------------- reportes

    /** ¿Qué cursos siguen sin docente en un periodo? */
    @GetMapping("/reporte-sin-docente/{idPeriodo}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<CursoDTOList>> reporteSinDocente(@PathVariable Long idPeriodo) {
        periodoService.listId(idPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + idPeriodo));

        List<CursoDTOList> lista = service.cursosSinDocente(idPeriodo)
                .stream()
                .map(x -> modelMapper.map(x, CursoDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

    /** ¿Para qué cursos hay que preparar material primero? */
    @GetMapping("/reporte-sin-material")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<CursoDTOList>> reporteSinMaterial() {

        List<CursoDTOList> lista = service.cursosSinMaterial()
                .stream()
                .map(x -> modelMapper.map(x, CursoDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

}
