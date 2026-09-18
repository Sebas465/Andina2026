package org.example.andina2026.controllers;

import jakarta.validation.Valid;
import org.example.andina2026.dtos.AulaDTOInsert;
import org.example.andina2026.dtos.AulaDTOList;
import org.example.andina2026.entities.Aula;
import org.example.andina2026.entities.Colegio;
import org.example.andina2026.exceptions.ResourceNotFoundException;
import org.example.andina2026.serviceinterfaces.AulaServiceInterface;
import org.example.andina2026.serviceinterfaces.ColegioServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aula")
public class AulaController {

    private final AulaServiceInterface ASI;
    private final ModelMapper MM;
    private final ColegioServiceInterface CSI;

    public AulaController(AulaServiceInterface ASI, ModelMapper MM, ColegioServiceInterface CSI) {
        this.ASI = ASI;
        this.MM = MM;
        this.CSI = CSI;
    }

    @GetMapping
    public ResponseEntity<List<AulaDTOList>> listar(){
        List<AulaDTOList> lista=ASI.list()
                .stream()
                .map(m->MM.map(m,AulaDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<AulaDTOInsert> registrar(@Valid @RequestBody AulaDTOInsert dto){
        Colegio st=CSI.listId(dto.getIdColegio())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el Colegio"+dto.getIdColegio()
                        ));
        Aula mv=MM.map(dto, Aula.class);
        mv.setIdAula(mv.getIdAula());
        ASI.insert(mv);
        AulaDTOInsert responseDTO=MM.map(mv,AulaDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(mv.getIdAula())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaDTOList>buscarid(@PathVariable Long id){
        Aula movie=ASI.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el aula: "+id
                        ));
        AulaDTOList respondeDTO=MM.map(movie,AulaDTOList.class);
        return ResponseEntity.ok(respondeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Long id){
        Aula movie=ASI.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el aula: "+id
                        ));
        ASI.delete(movie.getIdAula());
        return ResponseEntity.noContent().build();
    }

}
