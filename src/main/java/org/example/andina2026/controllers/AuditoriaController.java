package org.example.andina2026.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.andina2026.dtos.AuditoriaDTO;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;

import java.util.List;

/** Historial de cambios: /api/auditoria?entidad=Persona&idRegistro=1000 (H1.1 y H2.2). */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
    private final AuditoriaServiceInterface service;
    private final ModelMapper modelMapper;

    public AuditoriaController(AuditoriaServiceInterface service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<List<AuditoriaDTO>> historial(@RequestParam(required = false) String entidad,
                                                     @RequestParam(required = false) Long idRegistro) {
        List<AuditoriaDTO> lista = service.historial(entidad, idRegistro)
                .stream()
                .map(a -> modelMapper.map(a, AuditoriaDTO.class))
                .toList();
        return ResponseEntity.ok(lista);
    }
}
