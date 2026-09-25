package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.PersonaDTOInsert;
import org.example.andina2026.dtos.PersonaDTOList;
import org.example.andina2026.entities.Persona;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.PersonaServiceInterface;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.RolServiceInterface;
import org.example.andina2026.serviceinterfaces.AuditoriaServiceInterface;
import org.example.andina2026.entities.Aula;
import org.example.andina2026.entities.Rol;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    private final PersonaServiceInterface service;
    private final AulaServiceInterface aulaService;
    private final RolServiceInterface rolService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper modelMapper;

    public PersonaController(PersonaServiceInterface service, AulaServiceInterface aulaService, RolServiceInterface rolService, AuditoriaServiceInterface auditoria, ModelMapper modelMapper) {
        this.service = service;
        this.aulaService = aulaService;
        this.rolService = rolService;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PersonaDTOList>> listar() {
        List<PersonaDTOList> lista = service.list()
                .stream()
                .map(e -> toList(e))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<PersonaDTOInsert> buscarPorId(@PathVariable Long id) {
        Persona e = buscar(id);
        return ResponseEntity.ok(toDetail(e));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<PersonaDTOInsert> registrar(@Valid @RequestBody PersonaDTOInsert dto) {
        Persona e = modelMapper.map(dto, Persona.class);
        e.setIdPersona(null);
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        e.setCodigoEstudiante(nuevoCodigoEstudiante());
        validar(e, null);
        service.insert(e);
        auditoria.registrar("Persona", e.getIdPersona(), "CREAR", "Registro creado");
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(e.getIdPersona())
                .toUri();
        return ResponseEntity.created(location).body(toDetail(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<PersonaDTOInsert> modificar(@PathVariable Long id, @Valid @RequestBody PersonaDTOInsert dto) {
        Persona anterior = buscar(id);
        Persona e = modelMapper.map(dto, Persona.class);
        e.setIdPersona(id);
        e.setCodigoEstudiante(anterior.getCodigoEstudiante());
        e.setAula(aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula())));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        validar(e, id);
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getNombres(), e.getNombres())) cambios.add("nombres");
        if (!Objects.equals(anterior.getApellidos(), e.getApellidos())) cambios.add("apellidos");
        if (!Objects.equals(anterior.getFechaNacimiento(), e.getFechaNacimiento())) cambios.add("fechaNacimiento");
        if (!Objects.equals(anterior.getCorreo(), e.getCorreo())) cambios.add("correo");
        if (!Objects.equals(anterior.getLenguaMaterna(), e.getLenguaMaterna())) cambios.add("lenguaMaterna");
        if (!Objects.equals(anterior.getEstado(), e.getEstado())) cambios.add("estado");
        if (!Objects.equals(idDe(anterior.getAula()), idDe(e.getAula()))) cambios.add("idAula");
        if (!Objects.equals(idDe(anterior.getRol()), idDe(e.getRol()))) cambios.add("idRol");
        service.update(e);
        auditoria.registrar("Persona", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toDetail(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getIdPersona());
        auditoria.registrar("Persona", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private Persona buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + id));
    }

    private static Long idDe(Aula x) {
        return x == null ? null : x.getIdAula();
    }

    private static Long idDe(Rol x) {
        return x == null ? null : x.getIdTipoPersona();
    }

    private void validar(Persona e, Long id) {
        // H2.2: un alumno necesita edad 12-16, lengua materna y cupo en su aula
        if (e.getRol() != null && "ALUMNO".equalsIgnoreCase(e.getRol().getDetalle())) {
            if (e.getFechaNacimiento() == null) {
                throw new IllegalArgumentException("fechaNacimiento es obligatoria para un alumno");
            }
            int edad = java.time.Period.between(e.getFechaNacimiento(), java.time.LocalDate.now()).getYears();
            if (edad < 12 || edad > 16) {
                throw new IllegalArgumentException("Un alumno de 1° de secundaria debe tener entre 12 y 16 años (tiene " + edad + ")");
            }
            if (e.getLenguaMaterna() == null) {
                throw new IllegalArgumentException("lenguaMaterna es obligatoria para un alumno (QUECHUA, CASTELLANO o AMBOS)");
            }
            long enAula = service.list().stream()
                    .filter(p -> p.getAula() != null && p.getAula().getIdAula().equals(e.getAula().getIdAula()))
                    .filter(p -> p.getRol() != null && "ALUMNO".equalsIgnoreCase(p.getRol().getDetalle()))
                    .filter(p -> id == null || !p.getIdPersona().equals(id))
                    .count();
            if (enAula >= e.getAula().getCapacidad()) {
                throw new IllegalArgumentException("El aula " + e.getAula().getNombre() + " está llena (capacidad " + e.getAula().getCapacidad() + ")");
            }
        }
    }

    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    /** H2.2: ID estudiantil anonimizado (LPDP): no deriva de ningún dato personal. */
    private String nuevoCodigoEstudiante() {
        String codigo;
        do {
            codigo = String.format("EST-%08X", RANDOM.nextInt() & 0x7fffffff);
        } while (existeCodigo(codigo));
        return codigo;
    }

    private boolean existeCodigo(String codigo) {
        return service.list().stream().anyMatch(p -> codigo.equals(p.getCodigoEstudiante()));
    }

    private PersonaDTOList toList(Persona e) {
        PersonaDTOList dto = modelMapper.map(e, PersonaDTOList.class);
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdRol(e.getRol() != null ? e.getRol().getIdTipoPersona() : null);
        return dto;
    }

    private PersonaDTOInsert toDetail(Persona e) {
        PersonaDTOInsert dto = modelMapper.map(e, PersonaDTOInsert.class);
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdRol(e.getRol() != null ? e.getRol().getIdTipoPersona() : null);
        return dto;
    }
}
