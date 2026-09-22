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
import org.example.andina2026.dtos.RendimientoColegioDTO;
import org.example.andina2026.dtos.ReporteAgrupadoDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final ModelMapper modelMapper;

    public ColegioController(ColegioServiceInterface service, AuditoriaServiceInterface auditoria, ModelMapper modelMapper) {
        this.service = service;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ColegioDTOList>> listar() {
        List<ColegioDTOList> lista = service.list()
                .stream()
                .map(e -> modelMapper.map(e, ColegioDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ColegioDTOList> buscarPorId(@PathVariable Long id) {
        Colegio e = buscar(id);
        return ResponseEntity.ok(modelMapper.map(e, ColegioDTOList.class));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColegioDTOList> registrar(@Valid @RequestBody ColegioDTOInsert dto) {
        Colegio e = modelMapper.map(dto, Colegio.class);
        e.setIdColegio(null);
        service.insert(e);
        auditoria.registrar("Colegio", e.getIdColegio(), "CREAR", "Registro creado");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdColegio())
                .toUri();
        return ResponseEntity.created(location).body(modelMapper.map(e, ColegioDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColegioDTOList> modificar(@PathVariable Long id, @Valid @RequestBody ColegioDTOInsert dto) {
        Colegio anterior = buscar(id);
        Colegio e = modelMapper.map(dto, Colegio.class);
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
        return ResponseEntity.ok(modelMapper.map(e, ColegioDTOList.class));
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


    // ---------------------------------------------------------------- reportes

    /** Nota mínima aprobatoria (escala vigesimal). */
    private static final double NOTA_MINIMA = 11.0;

    /** H1.1: escuelas sin actividad (cambios o matrículas) en los últimos N días. */
    @GetMapping("/reporte-inactivos")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<ColegioDTOList>> reporteInactivos(@RequestParam(defaultValue = "30") int dias) {
        if (dias < 1 || dias > 3650) {
            throw new IllegalArgumentException("dias debe estar entre 1 y 3650");
        }

        List<ColegioDTOList> lista = service.escuelasInactivas(LocalDateTime.now().minusDays(dias))
                .stream()
                .map(x -> modelMapper.map(x, ColegioDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

    /** ¿Qué colegio necesita más recursos? Promedio y % de desaprobados por colegio. */
    @GetMapping("/reporte-rendimiento")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RendimientoColegioDTO>> reporteRendimiento() {

        List<RendimientoColegioDTO> lista = service.rendimientoPorColegio(NOTA_MINIMA)
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

    /** ¿Cuánta demanda tiene cada colegio por periodo? Alumnos distintos matriculados. */
    @GetMapping("/reporte-matriculas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<ReporteAgrupadoDTO>> reporteMatriculas() {

        List<ReporteAgrupadoDTO> lista = service.matriculasPorColegioYPeriodo()
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
