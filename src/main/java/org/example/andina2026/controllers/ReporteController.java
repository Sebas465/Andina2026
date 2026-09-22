package org.example.andina2026.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<AlumnoRendimientoDTO>> alumnosConMenorPromedio(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) String lengua,
            @RequestParam(required = false) Long idGrado) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("limite debe estar entre 1 y 100");
        }
        if (lengua != null && !lengua.matches("QUECHUA|CASTELLANO|AMBOS")) {
            throw new IllegalArgumentException("lengua debe ser QUECHUA, CASTELLANO o AMBOS");
        }
        return ResponseEntity.ok(service.alumnosConMenorPromedio(limite, lengua, idGrado));
    }

    /** H1.1: escuelas sin actividad (cambios o matrículas) en los últimos N días. */
    @GetMapping("/escuelas-inactivas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<EscuelaInactivaDTO>> escuelasInactivas(@RequestParam(defaultValue = "30") int dias) {
        if (dias < 1 || dias > 3650) {
            throw new IllegalArgumentException("dias debe estar entre 1 y 3650");
        }
        return ResponseEntity.ok(service.escuelasInactivas(dias));
    }

    /** H6.2: la misma lista priorizada, exportable a CSV (se abre en Excel con tildes correctas). */
    @GetMapping(value = "/alumnos-menor-promedio/csv", produces = "text/csv")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> alumnosConMenorPromedioCsv(@RequestParam(defaultValue = "10") int limite,
                                                             @RequestParam(required = false) String lengua,
                                                             @RequestParam(required = false) Long idGrado) {
        List<AlumnoRendimientoDTO> lista = alumnosConMenorPromedio(limite, lengua, idGrado).getBody();
        StringBuilder csv = new StringBuilder("\uFEFFprioridad,idPersona,nombres,apellidos,aula,colegio,promedio\n");
        int i = 1;
        for (AlumnoRendimientoDTO a : lista) {
            csv.append(i++).append(',').append(a.getIdPersona()).append(',')
                    .append(celda(a.getNombres())).append(',').append(celda(a.getApellidos())).append(',')
                    .append(celda(a.getAula())).append(',').append(celda(a.getColegio())).append(',')
                    .append(a.getPromedio()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"alumnos_refuerzo.csv\"")
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .body(csv.toString());
    }

    /** Celda CSV segura: comillas escapadas y sin fórmulas (evita inyección CSV en Excel). */
    private static String celda(String v) {
        if (v == null) {
            return "";
        }
        String s = v;
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) {
            s = "'" + s;
        }
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/alumnos-en-riesgo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<List<AlumnoRiesgoDTO>> alumnosEnRiesgo() {
        return ResponseEntity.ok(service.alumnosEnRiesgo());
    }

    @GetMapping("/rendimiento-colegios")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RendimientoColegioDTO>> rendimientoPorColegio() {
        return ResponseEntity.ok(service.rendimientoPorColegio());
    }

    @GetMapping("/ocupacion-aulas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<OcupacionAulaDTO>> ocupacionDeAulas() {
        return ResponseEntity.ok(service.ocupacionDeAulas());
    }

    @GetMapping("/carga-docente")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<CargaDocenteDTO>> cargaDocente() {
        return ResponseEntity.ok(service.cargaDocente());
    }

    @GetMapping("/cursos-sin-docente/{idPeriodo}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinDocente(@PathVariable Long idPeriodo) {
        periodoService.listId(idPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + idPeriodo));
        return ResponseEntity.ok(service.cursosSinDocente(idPeriodo));
    }

    @GetMapping("/cursos-sin-material")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinMaterial() {
        return ResponseEntity.ok(service.cursosSinMaterial());
    }

    @GetMapping("/matriculas-colegio-periodo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<MatriculaColegioDTO>> matriculasPorColegioYPeriodo() {
        return ResponseEntity.ok(service.matriculasPorColegioYPeriodo());
    }
}
