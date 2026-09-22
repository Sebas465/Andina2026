package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.example.andina2026.dtos.UsersDTOInsert;
import org.example.andina2026.dtos.UsersDTOList;
import org.example.andina2026.entities.Users;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.UsersServiceInterface;

import java.net.URI;
import java.util.List;

/** Gestión de cuentas (solo ADMIN). Ninguna respuesta incluye la contraseña. */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsersController {
    private final UsersServiceInterface service;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UsersController(UsersServiceInterface service, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UsersDTOList>> listar() {
        return ResponseEntity.ok(service.list().stream().map(this::toList).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDTOList> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toList(buscar(id)));
    }

    @PostMapping
    public ResponseEntity<UsersDTOList> registrar(@Valid @RequestBody UsersDTOInsert dto) {
        if (service.existsDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI");
        }
        if (service.existsUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ya existe el usuario: " + dto.getUsername());
        }
        // HASHEO: la contraseña se convierte en hash BCrypt (PasswordEncoder de SecurityConfig) antes de guardarla
        String passwordHash = passwordEncoder.encode(dto.getPassword());
        Users u = service.insert(dto.getDni(), dto.getUsername(), passwordHash, dto.getRoles(), dto.getEnabled());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(u.getId())
                .toUri();
        return ResponseEntity.created(location).body(toList(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(buscar(id).getId());
        return ResponseEntity.noContent().build();
    }

    private Users buscar(Long id) {
        return service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id: " + id));
    }

    private UsersDTOList toList(Users u) {
        UsersDTOList dto = modelMapper.map(u, UsersDTOList.class);
        // los roles se guardan como "ROLE_X": en la respuesta se muestran sin el prefijo
        dto.setRoles(u.getRoles().stream().map(r -> r.getRol().replaceFirst("^ROLE_", "")).toList());
        return dto;
    }
}
