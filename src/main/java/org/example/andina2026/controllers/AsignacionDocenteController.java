package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.AsignacionDocenteDTOInsert;
import org.example.andina2026.dtos.AsignacionDocenteDTOList;
import org.example.andina2026.entities.AsignacionDocente;
import org.example.andina2026.dtos.ReporteAgrupadoDTO;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.AsignacionDocenteServiceInterface;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;
import org.example.andina2026.serviceinterfaces.CursoServiceInterface;
import org.example.andina2026.serviceinterfaces.PeriodoAcademicoServiceInterface;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/asignaciones-docentes")
public class AsignacionDocenteController {
    private final AsignacionDocenteServiceInterface service;
    private final AulaServiceInterface aulaService;
    private final ColegioServiceInterface colegioService;
    private final CursoServiceInterface cursoService;
    private final PeriodoAcademicoServiceInterface periodoAcademicoService;
    private final PersonaServiceInterface personaService;
    private final ModelMapper modelMapper;

    public AsignacionDocenteController(AsignacionDocenteServiceInterface service, AulaServiceInterface aulaService, ColegioServiceInterface colegioService, CursoServiceInterface cursoService, PeriodoAcademicoServiceInterface periodoAcademicoService, PersonaServiceInterface personaService, ModelMapper modelMapper) {
        this.service = service;
        this.aulaService = aulaService;
        this.colegioService = colegioService;
        this.cursoService = cursoService;
        this.periodoAcademicoService = periodoAcademicoService;
        this.personaService = personaService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AsignacionDocenteDTOList>> listar() {
        List<AsignacionDocenteDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AsignacionDocenteDTOList> buscarPorId(@PathVariable Long id) {
        AsignacionDocente e = buscar(id);
        return ResponseEntity.ok(toList(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<AsignacionDocenteDTOList> registrar(@Valid @RequestBody AsignacionDocenteDTOInsert dto) {
        AsignacionDocente e = modelMapper.map(dto, AsignacionDocente.class);
        e.setIdAsignacion(null);
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso())));
        e.setPeriodo(periodoAcademicoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + dto.getIdPeriodo())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        service.insert(e);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdAsignacion())
                .toUri();
        return ResponseEntity.created(location).body(toList(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<AsignacionDocenteDTOList> modificar(@PathVariable Long id, @Valid @RequestBody AsignacionDocenteDTOInsert dto) {
        buscar(id);
        AsignacionDocente e = modelMapper.map(dto, AsignacionDocente.class);
        e.setIdAsignacion(id);
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Curso con id: " + dto.getIdCurso())));
        e.setPeriodo(periodoAcademicoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe PeriodoAcademico con id: " + dto.getIdPeriodo())));
        e.setPersona(personaService.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + dto.getIdPersona())));
        e.setColegio(colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Colegio con id: " + dto.getIdColegio())));
        service.update(e);
        return ResponseEntity.ok(toList(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdAsignacion());
        return ResponseEntity.noContent().build();
    }

    private AsignacionDocente buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe AsignacionDocente con id: " + id));
    }

    private AsignacionDocenteDTOList toList(AsignacionDocente e) {
        AsignacionDocenteDTOList dto = modelMapper.map(e, AsignacionDocenteDTOList.class);
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdCurso(e.getCurso() != null ? e.getCurso().getIdCurso() : null);
        dto.setIdPeriodo(e.getPeriodo() != null ? e.getPeriodo().getIdPeriodo() : null);
        dto.setIdPersona(e.getPersona() != null ? e.getPersona().getIdPersona() : null);
        dto.setIdColegio(e.getColegio() != null ? e.getColegio().getIdColegio() : null);
        return dto;
    }

    // ---------------------------------------------------------------- reportes

    /** ¿Algún docente está sobrecargado? Cursos que dicta cada docente por periodo. */
    @GetMapping("/reporte-carga-docente")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<ReporteAgrupadoDTO>> reporteCargaDocente() {

        List<ReporteAgrupadoDTO> lista = service.cargaDocente()
                .stream()
                .map(item -> {
                    ReporteAgrupadoDTO dto = new ReporteAgrupadoDTO();

                    dto.setCategoria((String) item[0]);
                    dto.setCantidad(((Number) item[1]).intValue());

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(lista);
    }

}
