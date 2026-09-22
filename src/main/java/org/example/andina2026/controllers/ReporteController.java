package org.example.andina2026.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.andina2026.dtos.*;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.AsignacionDocenteServiceInterface;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;
import org.example.andina2026.serviceinterfaces.PerfilAcademicoServiceInterface;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reportes para tomar decisiones. Igual que en demoSM2 (MovieController /total):
 * la consulta nativa está en el repository, el service devuelve List<Object[]>
 * y aquí cada fila (item) se pasa a su DTO.
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    /** Nota mínima aprobatoria (escala vigesimal). */
    private static final double NOTA_MINIMA = 11.0;

    private final PerfilAcademicoServiceInterface perfilService;
    private final ColegioServiceInterface colegioService;
    private final AulaServiceInterface aulaService;
    private final AsignacionDocenteServiceInterface asignacionService;
    private final CursoServiceInterface cursoService;
    private final PeriodoAcademicoServiceInterface periodoService;

    public ReporteController(PerfilAcademicoServiceInterface perfilService, ColegioServiceInterface colegioService,
                             AulaServiceInterface aulaService, AsignacionDocenteServiceInterface asignacionService,
                             CursoServiceInterface cursoService, PeriodoAcademicoServiceInterface periodoService) {
        this.perfilService = perfilService;
        this.colegioService = colegioService;
        this.aulaService = aulaService;
        this.asignacionService = asignacionService;
        this.cursoService = cursoService;
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

        List<AlumnoRendimientoDTO> lista = perfilService.alumnosConMenorPromedio(limite, lengua, idGrado)
                .stream()
                .map(item -> {
                    AlumnoRendimientoDTO dto = new AlumnoRendimientoDTO();

                    dto.setIdPersona(((Number) item[0]).longValue());
                    dto.setNombres((String) item[1]);
                    dto.setApellidos((String) item[2]);
                    dto.setAula((String) item[3]);
                    dto.setColegio((String) item[4]);
                    dto.setPromedio(new BigDecimal(item[5].toString()));

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    /** H1.1: escuelas sin actividad (cambios o matrículas) en los últimos N días. */
    @GetMapping("/escuelas-inactivas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<EscuelaInactivaDTO>> escuelasInactivas(@RequestParam(defaultValue = "30") int dias) {
        if (dias < 1 || dias > 3650) {
            throw new IllegalArgumentException("dias debe estar entre 1 y 3650");
        }
        LocalDateTime ahora = LocalDateTime.now();

        List<EscuelaInactivaDTO> lista = colegioService.escuelasInactivas(ahora.minusDays(dias))
                .stream()
                .map(item -> {
                    EscuelaInactivaDTO dto = new EscuelaInactivaDTO();

                    dto.setIdColegio(((Number) item[0]).longValue());
                    dto.setColegio((String) item[1]);
                    dto.setCodigoModular((String) item[2]);
                    // última actividad: null si la escuela nunca tuvo movimiento
                    LocalDateTime ultima = item[3] == null ? null
                            : item[3] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) item[3];
                    dto.setUltimaActividad(ultima);
                    dto.setDiasSinActividad(ultima == null ? null : Duration.between(ultima, ahora).toDays());

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
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

        List<AlumnoRiesgoDTO> lista = perfilService.alumnosEnRiesgo(NOTA_MINIMA)
                .stream()
                .map(item -> {
                    AlumnoRiesgoDTO dto = new AlumnoRiesgoDTO();

                    dto.setIdPersona(((Number) item[0]).longValue());
                    dto.setNombres((String) item[1]);
                    dto.setApellidos((String) item[2]);
                    dto.setAula((String) item[3]);
                    dto.setPromedio(new BigDecimal(item[4].toString()));
                    dto.setTieneObservacionPsicologica(true); // la consulta ya filtra a quienes tienen observación

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/rendimiento-colegios")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RendimientoColegioDTO>> rendimientoPorColegio() {

        List<RendimientoColegioDTO> lista = colegioService.rendimientoPorColegio(NOTA_MINIMA)
                .stream()
                .map(item -> {
                    RendimientoColegioDTO dto = new RendimientoColegioDTO();

                    dto.setIdColegio(((Number) item[0]).longValue());
                    dto.setColegio((String) item[1]);
                    dto.setTipoZona((String) item[2]);
                    dto.setAlumnosEvaluados(((Number) item[3]).longValue());
                    dto.setPromedio(new BigDecimal(item[4].toString()));
                    dto.setDesaprobados(((Number) item[5]).longValue());
                    dto.setPorcentajeDesaprobados(new BigDecimal(item[6].toString()));

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/ocupacion-aulas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<OcupacionAulaDTO>> ocupacionDeAulas() {

        List<OcupacionAulaDTO> lista = aulaService.ocupacionDeAulas()
                .stream()
                .map(item -> {
                    OcupacionAulaDTO dto = new OcupacionAulaDTO();

                    dto.setIdAula(((Number) item[0]).longValue());
                    dto.setAula((String) item[1]);
                    dto.setSeccion((String) item[2]);
                    dto.setColegio((String) item[3]);
                    dto.setCapacidad(((Number) item[4]).intValue());
                    dto.setAlumnos(((Number) item[5]).longValue());
                    dto.setPorcentajeOcupacion(item[6] == null ? null : new BigDecimal(item[6].toString()));

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/carga-docente")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<CargaDocenteDTO>> cargaDocente() {

        List<CargaDocenteDTO> lista = asignacionService.cargaDocente()
                .stream()
                .map(item -> {
                    CargaDocenteDTO dto = new CargaDocenteDTO();

                    dto.setIdPersona(((Number) item[0]).longValue());
                    dto.setNombres((String) item[1]);
                    dto.setApellidos((String) item[2]);
                    dto.setPeriodo((String) item[3]);
                    dto.setCursos(((Number) item[4]).longValue());
                    dto.setHorasSemanales(new BigDecimal(item[5].toString()));

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/cursos-sin-docente/{idPeriodo}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinDocente(@PathVariable Long idPeriodo) {
        periodoService.listId(idPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + idPeriodo));

        List<CursoPendienteDTO> lista = cursoService.cursosSinDocente(idPeriodo)
                .stream()
                .map(item -> {
                    CursoPendienteDTO dto = new CursoPendienteDTO();

                    dto.setIdCurso(((Number) item[0]).longValue());
                    dto.setCurso((String) item[1]);
                    dto.setArea((String) item[2]);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/cursos-sin-material")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<CursoPendienteDTO>> cursosSinMaterial() {

        List<CursoPendienteDTO> lista = cursoService.cursosSinMaterial()
                .stream()
                .map(item -> {
                    CursoPendienteDTO dto = new CursoPendienteDTO();

                    dto.setIdCurso(((Number) item[0]).longValue());
                    dto.setCurso((String) item[1]);
                    dto.setArea((String) item[2]);

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/matriculas-colegio-periodo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<MatriculaColegioDTO>> matriculasPorColegioYPeriodo() {

        List<MatriculaColegioDTO> lista = colegioService.matriculasPorColegioYPeriodo()
                .stream()
                .map(item -> {
                    MatriculaColegioDTO dto = new MatriculaColegioDTO();

                    dto.setIdColegio(((Number) item[0]).longValue());
                    dto.setColegio((String) item[1]);
                    dto.setPeriodo((String) item[2]);
                    dto.setAlumnosMatriculados(((Number) item[3]).longValue());

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }
}
