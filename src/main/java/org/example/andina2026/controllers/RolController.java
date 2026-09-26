package org.example.andina2026.controllers;

import org.example.andina2026.dtos.RolDTOInsert;
import org.example.andina2026.dtos.RolDTOList;
import org.example.andina2026.servicesinterfaces.IRolService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
            @Valid @RequestBody StreamingDTOInsert dto) {
        Streaming st = modelMapper.map(dto, Streaming.class);
        cS.insert(st);
        StreamingDTOInsert responseDTO =
                modelMapper.map(st, StreamingDTOInsert.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(st.getIdStreaming())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(responseDTO);
    }
}