package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.PerfilAcademicoDTOInsert;
import org.example.andina2026.dtos.PerfilAcademicoDTOList;
import org.example.andina2026.entities.PerfilAcademico;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PerfilAcademicoServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;
import org.example.andina2026.entities.Persona;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/perfiles-academicos")
public class PerfilAcademicoController {
    private final PerfilAcademicoServiceInterface service;
    private final PersonaServiceInterface personaService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper modelMapper;

    public PerfilAcademicoController(PerfilAcademicoServiceInterface service, PersonaServiceInterface personaService, AuditoriaServiceInterface auditoria, ModelMapper modelMapper) {
        this.service = service;
        this.personaService = personaService;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PerfilAcademicoDTOList>> listar() {
        List<PerfilAcademicoDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<PerfilAcademicoDTOInsert> buscarPorId(@PathVariable Long id) {
        PerfilAcademico e = buscar(id);
        return ResponseEntity.ok(toDetail(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<PerfilAcademicoDTOInsert> registrar(@Valid @RequestBody PerfilAcademicoDTOInsert dto) {
        PerfilAcademico e = modelMapper.map(dto, PerfilAcademico.class);
        e.setIdPerfilAcademico(null);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        auditoria.registrar("PerfilAcademico", e.getIdPerfilAcademico(), "CREAR", "Registro creado");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdPerfilAcademico())
                .toUri();
        return ResponseEntity.created(location).body(toDetail(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<PerfilAcademicoDTOInsert> modificar(@PathVariable Long id, @Valid @RequestBody PerfilAcademicoDTOInsert dto) {
        PerfilAcademico anterior = buscar(id);
        PerfilAcademico e = modelMapper.map(dto, PerfilAcademico.class);
        e.setIdPerfilAcademico(id);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getDetalles(), e.getDetalles())) cambios.add("detalles");
        if (!Objects.equals(anterior.getNotas(), e.getNotas())) cambios.add("notas");
        if (!Objects.equals(anterior.getEstadoPsicologico(), e.getEstadoPsicologico())) cambios.add("estadoPsicologico");
        if (!Objects.equals(idDe(anterior.getPersona()), idDe(e.getPersona()))) cambios.add("idPersona");
        service.update(e);
        auditoria.registrar("PerfilAcademico", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toDetail(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdPerfilAcademico());
        auditoria.registrar("PerfilAcademico", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private PerfilAcademico buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PerfilAcademico con id: " + id));
    }

    private static Long idDe(Persona x) {
        return x == null ? null : x.getIdPersona();
    }

    private PerfilAcademicoDTOList toList(PerfilAcademico e) {
        PerfilAcademicoDTOList dto = modelMapper.map(e, PerfilAcademicoDTOList.class);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }

    private PerfilAcademicoDTOInsert toDetail(PerfilAcademico e) {
        PerfilAcademicoDTOInsert dto = modelMapper.map(e, PerfilAcademicoDTOInsert.class);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
