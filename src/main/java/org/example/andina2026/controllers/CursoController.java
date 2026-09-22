package pe.edu.upc.demosm2.controllers;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.CursoDTOInsert;
import pe.edu.upc.demosm2.dtos.CursoDTOList;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.servicesinterfaces.ICursoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {
    public final ICursoService cS;
    public final ModelMapper modelMapper;

    public CursoController(ICursoService cS, ModelMapper modelMapper) {
        this.cS = cS;
        this.modelMapper = modelMapper;
    }


    @PostMapping
    public ResponseEntity<CursoDTOInsert> resgitrar (@Valid @RequestBody CursoDTOInsert dto){
        Curso q = modelMapper.map(dto, Curso.class);
        cS.insert(q);
        CursoDTOInsert registro = modelMapper.map(dto, CursoDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(q.getId_curso())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(registro);
    }

    @GetMapping
    public ResponseEntity <List<CursoDTOList>> listar(){
        List<CursoDTOList> lista= cS.list()
                .stream()
                .map(curso -> modelMapper.map(curso, CursoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTOList> buscarcurso(@PathVariable Long id){
        Curso curso = cS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el curso, intente de nuevo: "+ id
                        ));
        CursoDTOList respuesta = modelMapper.map(curso,CursoDTOList.class);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        Curso curso = cS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el curso, intente de nuevo: "+ id
                        ));
        cS.delete(curso.getId_curso());
        return ResponseEntity.noContent().build();
    }
}
