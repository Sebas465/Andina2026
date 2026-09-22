package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.MaterialCursoDTO;
import org.example.andina2026.entities.Curso;
import org.example.andina2026.entities.Material;
import org.example.andina2026.entities.MaterialCurso;
import org.example.andina2026.entities.MaterialCursoId;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;
import org.example.andina2026.serviceinterfaces.MaterialCursoServiceInterface;
import org.example.andina2026.serviceinterfaces.MaterialServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/materiales-cursos")
public class MaterialCursoController {
    private final MaterialCursoServiceInterface service;
    private final MaterialServiceInterface materialService;
    private final CursoServiceInterface cursoService;

    public MaterialCursoController(MaterialCursoServiceInterface service, MaterialServiceInterface materialService,
                                   CursoServiceInterface cursoService) {
        this.service = service;
        this.materialService = materialService;
        this.cursoService = cursoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MaterialCursoDTO>> listar() {
        List<MaterialCursoDTO> lista = service.list()
                .stream()
                .map(mc -> new MaterialCursoDTO(mc.getId().getIdMaterial(), mc.getId().getIdCurso()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MaterialCursoDTO> registrar(@Valid @RequestBody MaterialCursoDTO dto) {
        Material material = materialService.listId(dto.getIdMaterial())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Material con id: " + dto.getIdMaterial()));
        Curso curso = cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso()));
        service.insert(new MaterialCurso(material, curso));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idMaterial}/{idCurso}")
                .buildAndExpand(dto.getIdMaterial(), dto.getIdCurso())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @DeleteMapping("/{idMaterial}/{idCurso}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long idMaterial, @PathVariable Long idCurso) {
        MaterialCursoId id = new MaterialCursoId(idMaterial, idCurso);
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la relación material " + idMaterial + " - curso " + idCurso));
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
