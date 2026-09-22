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
import org.example.andina2026.dtos.AlumnoRendimientoDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
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

    // ---------------------------------------------------------------- reportes

    /** Nota mínima aprobatoria (escala vigesimal). */
    private static final double NOTA_MINIMA = 11.0;

    /** ¿A quién apoyar primero? Los alumnos con el promedio más bajo (filtros: lengua materna y grado). */
    @GetMapping("/reporte-menor-promedio")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<AlumnoRendimientoDTO>> reporteMenorPromedio(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) String lengua,
            @RequestParam(required = false) Long idGrado) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("limite debe estar entre 1 y 100");
        }
        if (lengua != null && !lengua.matches("QUECHUA|CASTELLANO|AMBOS")) {
            throw new IllegalArgumentException("lengua debe ser QUECHUA, CASTELLANO o AMBOS");
        }

        List<AlumnoRendimientoDTO> lista = service.alumnosConMenorPromedio(limite, lengua, idGrado)
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

    /** H6.2: la misma lista priorizada, exportable a CSV (se abre en Excel con tildes correctas). */
    @GetMapping(value = "/reporte-menor-promedio/csv", produces = "text/csv")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> reporteMenorPromedioCsv(@RequestParam(defaultValue = "10") int limite,
                                                          @RequestParam(required = false) String lengua,
                                                          @RequestParam(required = false) Long idGrado) {
        List<AlumnoRendimientoDTO> lista = reporteMenorPromedio(limite, lengua, idGrado).getBody();
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

}
