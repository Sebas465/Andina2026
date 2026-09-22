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
import org.example.andina2026.serviceinterfaces.GradoServiceInterface;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;
import org.example.andina2026.entities.Colegio;
import org.example.andina2026.entities.Grado;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/aula")
public class AulaController {
    private final AulaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final GradoServiceInterface gradoService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper modelMapper;

    public AulaController(AulaServiceInterface service, ColegioServiceInterface colegioService, GradoServiceInterface gradoService, AuditoriaServiceInterface auditoria, ModelMapper modelMapper) {
        this.service = service;
        this.colegioService = colegioService;
        this.gradoService = gradoService;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
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
        Aula e = modelMapper.map(dto, Aula.class);
        e.setIdAula(null);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        List<Grado> grados = new ArrayList<>();
        for (Long idX : dto.getIdGrados() == null ? List.<Long>of() : dto.getIdGrados()) {
            grados.add(gradoService.listId(idX)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + idX)));
        }
        e.setGrados(grados);
        service.insert(e);
        auditoria.registrar("Aula", e.getIdAula(), "CREAR", "Registro creado");
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
        Aula anterior = buscar(id);
        Aula e = modelMapper.map(dto, Aula.class);
        e.setIdAula(id);
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        List<Grado> grados = new ArrayList<>();
        for (Long idX : dto.getIdGrados() == null ? List.<Long>of() : dto.getIdGrados()) {
            grados.add(gradoService.listId(idX)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Grado con id: " + idX)));
        }
        e.setGrados(grados);
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getNombre(), e.getNombre())) cambios.add("nombre");
        if (!Objects.equals(anterior.getSeccion(), e.getSeccion())) cambios.add("seccion");
        if (!Objects.equals(anterior.getCapacidad(), e.getCapacidad())) cambios.add("capacidad");
        if (!Objects.equals(anterior.getComputadoras(), e.getComputadoras())) cambios.add("computadoras");
        if (!Objects.equals(anterior.getProyectores(), e.getProyectores())) cambios.add("proyectores");
        if (!Objects.equals(anterior.getConexionMbps(), e.getConexionMbps())) cambios.add("conexionMbps");
        if (!Objects.equals(idDe(anterior.getColegio()), idDe(e.getColegio()))) cambios.add("idColegio");
        service.update(e);
        auditoria.registrar("Aula", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdAula());
        auditoria.registrar("Aula", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private Aula buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + id));
    }

    private static Long idDe(Colegio x) {
        return x == null ? null : x.getIdColegio();
    }

    private AulaDTOList toList(Aula e) {
        AulaDTOList dto = modelMapper.map(e, AulaDTOList.class);
        dto.setIdColegio(e.getColegio() != null ? e.getColegio().getIdColegio() : null);
        dto.setIdGrados(e.getGrados().stream().map(Grado::getIdGrado).toList());
        return dto;
    }
}
