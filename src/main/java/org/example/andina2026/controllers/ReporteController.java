package org.example.andina2026.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.andina2026.dtos.*;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;
import org.example.andina2026.serviceinterfaces.ReporteServiceInterface;

import java.util.List;

/** Reportes para tomar decisiones (consultas nativas). Nunca exponen contraseñas ni textos clínicos. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteServiceInterface service;
    private final PeriodoAcademicoServiceInterface periodoService;

    public ReporteController(ReporteServiceInterface service, PeriodoAcademicoServiceInterface periodoService) {
        this.service = service;
        this.periodoService = periodoService;
    }

    @GetMapping("/alumnos-menor-promedio")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','PSICOLOGO')")
    public ResponseEntity<List<AlumnoRendimientoDTO>> alumnosConMenorPromedio(@RequestParam(defaultValue = "10") int limite) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("limite debe estar entre 1 y 100");
        }
        return ResponseEntity.ok(service.alumnosConMenorPromedio(limite));
    }

    @GetMapping("/alumnos-en-riesgo")
    @PreAuthorize("hasAnyRole('ADMIN','PSICOLOGO')")
    public ResponseEntity<List<AlumnoRiesgoDTO>> alumnosEnRiesgo() {
        return ResponseEntity.ok(service.alumnosEnRiesgo());
    }

    @GetMapping("/rendimiento-colegios")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<List<RendimientoColegioDTO>> rendimientoPorColegio() {
        return ResponseEntity.ok(service.rendimientoPorColegio());
    }

    @GetMapping("/ocupacion-aulas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OcupacionAulaDTO>> ocupacionDeAulas() {
        return ResponseEntity.ok(service.ocupacionDeAulas());
    }

    @GetMapping("/carga-docente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CargaDocenteDTO>> cargaDocente() {
        return ResponseEntity.ok(service.cargaDocente());
    }

    @GetMapping("/cursos-sin-docente/{idPeriodo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinDocente(@PathVariable Long idPeriodo) {
        periodoService.listId(idPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + idPeriodo));
        return ResponseEntity.ok(service.cursosSinDocente(idPeriodo));
    }

    @GetMapping("/cursos-sin-material")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinMaterial() {
        return ResponseEntity.ok(service.cursosSinMaterial());
    }

    @GetMapping("/matriculas-colegio-periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatriculaColegioDTO>> matriculasPorColegioYPeriodo() {
        return ResponseEntity.ok(service.matriculasPorColegioYPeriodo());
    }
}
