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
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/colegios")
public class ColegioController {
    private final ColegioServiceInterface service;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper MM;

    public ColegioController(ColegioServiceInterface service, AuditoriaServiceInterface auditoria, ModelMapper MM) {
        this.service = service;
        this.auditoria = auditoria;
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
        auditoria.registrar("Colegio", e.getIdColegio(), "CREAR", "Registro creado");
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
        Colegio anterior = buscar(id);
        Colegio e = MM.map(dto, Colegio.class);
        e.setIdColegio(id);
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getCodigoModular(), e.getCodigoModular())) cambios.add("codigoModular");
        if (!Objects.equals(anterior.getNombre(), e.getNombre())) cambios.add("nombre");
        if (!Objects.equals(anterior.getDepartamento(), e.getDepartamento())) cambios.add("departamento");
        if (!Objects.equals(anterior.getProvincia(), e.getProvincia())) cambios.add("provincia");
        if (!Objects.equals(anterior.getDistrito(), e.getDistrito())) cambios.add("distrito");
        if (!Objects.equals(anterior.getComunidad(), e.getComunidad())) cambios.add("comunidad");
        if (!Objects.equals(anterior.getTipo_zona(), e.getTipo_zona())) cambios.add("tipo_zona");
        service.update(e);
        auditoria.registrar("Colegio", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdColegio());
        auditoria.registrar("Colegio", id, "ELIMINAR", "Registro eliminado");
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
//Commit