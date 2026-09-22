package org.example.andina2026.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.andina2026.entities.Auditoria;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;

import java.util.List;

/** Historial de cambios: /api/auditoria?entidad=Persona&idRegistro=1000 (H1.1 y H2.2). */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
    private final AuditoriaServiceInterface service;

    public AuditoriaController(AuditoriaServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<List<Auditoria>> historial(@RequestParam(required = false) String entidad,
                                                     @RequestParam(required = false) Long idRegistro) {
        return ResponseEntity.ok(service.historial(entidad, idRegistro));
    }
}
