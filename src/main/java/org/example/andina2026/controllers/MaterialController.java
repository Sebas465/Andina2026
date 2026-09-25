package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.MaterialDTOInsert;
import org.example.andina2026.dtos.MaterialDTOList;
import org.example.andina2026.entities.Material;
// Adaptado a master: las excepciones viven en el paquete pe.edu.upc.demosm2 (se integra con su GlobalExceptionHandler)
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.MaterialServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/materiales")
public class MaterialController {
    private final MaterialServiceInterface service;
    private final PersonaServiceInterface personaService;

    // Adaptado a master: sin ModelMapper (su bean no está en el escaneo). El mapeo DTO<->entidad se hace a mano.
    public MaterialController(MaterialServiceInterface service, PersonaServiceInterface personaService) {
        this.service = service;
        this.personaService = personaService;
    }

    /** Copia los campos del DTO a una entidad Material (sin tocar la persona ni el id). */
    private Material fromDto(MaterialDTOInsert dto) {
        Material e = new Material();
        e.setTitulo(dto.getTitulo());
        e.setDescripcion(dto.getDescripcion());
        e.setTipo(dto.getTipo());
        e.setUrlArchivo(dto.getUrlArchivo());
        e.setFechaPublicacion(dto.getFechaPublicacion());
        return e;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MaterialDTOList>> listar() {
        List<MaterialDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialDTOList> buscarPorId(@PathVariable Long id) {
        Material e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MaterialDTOList> registrar(@Valid @RequestBody MaterialDTOInsert dto) {
        Material e = fromDto(dto);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdMaterial())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MaterialDTOList> modificar(@PathVariable Long id, @Valid @RequestBody MaterialDTOInsert dto) {
        buscar(id);
        Material e = fromDto(dto);
        e.setIdMaterial(id);
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdMaterial());
        return ResponseEntity.noContent().build();
    }

    private Material buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Material con id: " + id));
    }

    private MaterialDTOList toList(Material e) {
        MaterialDTOList dto = new MaterialDTOList();
        dto.setIdMaterial(e.getIdMaterial());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setTipo(e.getTipo());
        dto.setUrlArchivo(e.getUrlArchivo());
        dto.setFechaPublicacion(e.getFechaPublicacion());
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        return dto;
    }
}
