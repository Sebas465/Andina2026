package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Set;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    private final PersonaServiceInterface service;
    private final AulaServiceInterface aulaService;
    private final RolServiceInterface rolService;
    private final AuditoriaServiceInterface auditoria;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /** Tipos de persona que dan acceso al sistema (Word H2.1): solo el ADMIN los asigna. */
    private static final Set<String> ROLES_DE_ACCESO =
            Set.of("ROLE_ADMIN", "ROLE_ADMIN_ESCUELA", "ROLE_ESPECIALISTA", "ROLE_LOCAL");

    public PersonaController(PersonaServiceInterface service, AulaServiceInterface aulaService, RolServiceInterface rolService, AuditoriaServiceInterface auditoria, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.aulaService = aulaService;
        this.rolService = rolService;
        this.auditoria = auditoria;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
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
        e.setAula(aulaDe(dto));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        e.setCodigoEstudiante(nuevoCodigoEstudiante());
        aplicarCuenta(e, dto, null);
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
        e.setAula(aulaDe(dto));
        e.setRol(rolService.listId(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Rol con id: " + dto.getIdRol())));
        aplicarCuenta(e, dto, anterior);
        validar(e, id);
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(anterior.getNombres(), e.getNombres())) cambios.add("nombres");
        if (!Objects.equals(anterior.getApellidos(), e.getApellidos())) cambios.add("apellidos");
        if (!Objects.equals(anterior.getFechaNacimiento(), e.getFechaNacimiento())) cambios.add("fechaNacimiento");
        if (!Objects.equals(anterior.getCorreo(), e.getCorreo())) cambios.add("correo");
        if (!Objects.equals(anterior.getLenguaMaterna(), e.getLenguaMaterna())) cambios.add("lenguaMaterna");
        if (!Objects.equals(anterior.getEstado(), e.getEstado())) cambios.add("estado");
        if (!Objects.equals(anterior.getDni(), e.getDni())) cambios.add("dni");
        if (!Objects.equals(anterior.getPassword(), e.getPassword())) cambios.add("password");
        if (!Objects.equals(anterior.getEnabled(), e.getEnabled())) cambios.add("enabled");
        if (!Objects.equals(idDe(anterior.getAula()), idDe(e.getAula()))) cambios.add("idAula");
        if (!Objects.equals(idDe(anterior.getRol()), idDe(e.getRol()))) cambios.add("idRol");
        service.update(e);
        auditoria.registrar("Persona", id, "MODIFICAR", cambios.isEmpty() ? "Sin cambios" : "Campos modificados: " + String.join(", ", cambios));
        return ResponseEntity.ok(toDetail(e));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Persona e = buscar(id);
        if (tieneAcceso(e)) {
            soloAdmin();
        }
        service.delete(e.getIdPersona());
        auditoria.registrar("Persona", id, "ELIMINAR", "Registro eliminado");
        return ResponseEntity.noContent().build();
    }

    private Persona buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe Persona con id: " + id));
    }

    private Aula aulaDe(PersonaDTOInsert dto) {
        if (dto.getIdAula() == null) {
            return null;
        }
        return aulaService.listId(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Aula con id: " + dto.getIdAula()));
    }

    /**
     * H2.1: la persona es también el usuario (antes tabla «users»). La contraseña, la cuenta habilitada y los
     * tipos con acceso (ADMIN, ADMIN_ESCUELA, ESPECIALISTA, LOCAL) solo los gestiona el ADMIN: así un LOCAL
     * no puede crearse una cuenta ni cambiar la de otro.
     */
    private void aplicarCuenta(Persona e, PersonaDTOInsert dto, Persona anterior) {
        boolean tocaCuenta = dto.getPassword() != null || dto.getEnabled() != null || tieneAcceso(e)
                || (anterior != null && tieneAcceso(anterior));
        if (tocaCuenta) {
            soloAdmin();
        }
        if (e.getDni() != null) {
            service.buscarPorDni(e.getDni())
                    .filter(otra -> !otra.getIdPersona().equals(e.getIdPersona()))
                    .ifPresent(otra -> {
                        throw new IllegalArgumentException("Ya existe una persona con ese DNI");
                    });
        }
        // HASHEO: la contraseña se guarda como hash BCrypt (PasswordEncoder de SecurityConfig); vacía = se conserva
        if (dto.getPassword() != null) {
            e.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            e.setPassword(anterior != null ? anterior.getPassword() : null);
        }
        if (dto.getEnabled() != null) {
            e.setEnabled(dto.getEnabled());
        } else {
            e.setEnabled(anterior == null || Boolean.TRUE.equals(anterior.getEnabled()));
        }
        if (e.getPassword() != null && e.getDni() == null) {
            throw new IllegalArgumentException("dni es obligatorio para una persona con cuenta");
        }
    }

    /** ¿Puede iniciar sesión o su tipo da permisos? */
    private static boolean tieneAcceso(Persona p) {
        return p.getPassword() != null || (p.getRol() != null && ROLES_DE_ACCESO.contains(p.getRol().getAuthority()));
    }

    private static void soloAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!admin) {
            throw new AccessDeniedException("Solo el ADMIN gestiona cuentas y tipos con acceso al sistema");
        }
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
            if (e.getAula() == null) {
                throw new IllegalArgumentException("idAula es obligatorio para un alumno");
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
        dto.setPassword(null); // nunca se devuelve (ni siquiera el hash)
        dto.setIdAula(e.getAula() != null ? e.getAula().getIdAula() : null);
        dto.setIdRol(e.getRol() != null ? e.getRol().getIdTipoPersona() : null);
        return dto;
    }

    // ---------------------------------------------------------------- reportes

    /** Nota mínima aprobatoria (escala vigesimal). */
    private static final double NOTA_MINIMA = 11.0;

    /** ¿A quién debe atender primero psicología? Alumnos desaprobados con observación psicológica. */
    @GetMapping("/reporte-en-riesgo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PersonaDTOList>> reporteEnRiesgo() {

        List<PersonaDTOList> lista = service.alumnosEnRiesgo(NOTA_MINIMA)
                .stream()
                .map(x -> toList(x))
                .toList();

        return ResponseEntity.ok(lista);
    }

}
