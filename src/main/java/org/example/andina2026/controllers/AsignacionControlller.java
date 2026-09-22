package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.AsignacionDTOInsert;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.servicesinterfaces.IAsignacionDocenteService;
import pe.edu.upc.demosm2.servicesinterfaces.ICursoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/asignacion")
public class AsignacionControlller {
    public final IAsignacionDocenteService aS;
    public final ICursoService cs;
    public final ModelMapper modelMapper;

    public AsignacionControlller(IAsignacionDocenteService aS, ICursoService cs, ModelMapper modelMapper) {
        this.aS = aS;
        this.cs = cs;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<AsignacionDTOInsert> registrar (@Valid @RequestBody AsignacionDTOInsert dto){
        Curso q = cs.listId(dto.getCurso())
        .orElseThrow(()->
                new ResourceNotFoundException(
                        "No existe el Curso: "+dto.getCurso()
                ));
        AsignacionDocente ag=modelMapper.map(dto, AsignacionDocente.class);
        ag.setId_asignacion(ag.getId_asignacion());
        aS.insert(ag);
        AsignacionDTOInsert registro = modelMapper.map(ag,AsignacionDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ag.getId_asignacion())
                .toUri();
        return ResponseEntity.created(location).body(registro);
    }

    @GetMapping
    public ResponseEntity <List<AsignacionDTOList>> listar(){
        List<AsignacionDTOList> lista= aS.list()
                .stream()
                .map(docente -> modelMapper.map(docente, AsignacionDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionDTOList> buscardocente(@PathVariable Long id){
       AsignacionDocente asignacionDocente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        AsignacionDTOList respuesta = modelMapper.map(asignacionDocente,AsignacionDTOList.class);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        AsignacionDocente docente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        aS.delete(docente.getId_asignacion());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<AsignacionDTOList>> ObtenerRangoSegunHoras(@RequestParam Long min, @RequestParam Long max){
        List<AsignacionDTOList> filtro= aS.findByHorassemanalesIsBetween(min, max);
        return ResponseEntity.ok(filtro);
    }
}
