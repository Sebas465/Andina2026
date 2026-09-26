package org.example.andina2026.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.example.andina2026.dtos.RolDTOInsert;
import org.example.andina2026.dtos.RolDTOList;
import org.example.andina2026.entities.Rol;
import org.example.andina2026.servicesinterfaces.IRolService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

public class RolController {
    private final IRolService rS;
    private final ModelMapper modelMapper;


    public RolController(IRolService rS, ModelMapper modelMapper) {
        this.rS = rS;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<RolDTOList>> listar() {
        List<RolDTOList> lista=rS.list()
                .stream()
                .map(rol -> modelMapper.map(rol, RolDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }
    //Post envia
    @PostMapping
    public ResponseEntity<RolDTOInsert> registrar(
            @Valid @RequestBody RolDTOInsert dto) {
        Rol ro = modelMapper.map(dto, Rol.class);
        rS.insert(ro);
        RolDTOInsert responseDTO =
                modelMapper.map(ro, RolDTOInsert.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ro.getIdTipoPersona())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(responseDTO);
    }

    @GetMapping("/identificador")
    public ResponseEntity<List<RolDTOInsert>>
            buscarporid(@RequestParam boolean r) {
        List<RolDTOInsert> lista = rS.
    }
}