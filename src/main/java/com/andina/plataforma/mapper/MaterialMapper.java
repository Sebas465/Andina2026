package com.andina.plataforma.mapper;

import com.andina.plataforma.dto.CursoResumenDTO;
import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;
import com.andina.plataforma.model.entity.Material;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MaterialMapper {

    public Material toEntity(MaterialRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Material.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .tipo(dto.getTipo())
                .urlArchivo(dto.getUrlArchivo())
                .fechaPublicacion(dto.getFechaPublicacion())
                .build();
    }

    public MaterialResponseDTO toResponseDTO(Material material) {
        if (material == null) {
            return null;
        }
        return MaterialResponseDTO.builder()
                .idMaterial(material.getIdMaterial())
                .idPersona(material.getPersona() != null ? material.getPersona().getIdPersona() : null)
                .nombrePersona(material.getPersona() != null
                        ? material.getPersona().getNombres() + " " + material.getPersona().getApellidos()
                        : null)
                .titulo(material.getTitulo())
                .descripcion(material.getDescripcion())
                .tipo(material.getTipo())
                .urlArchivo(material.getUrlArchivo())
                .fechaPublicacion(material.getFechaPublicacion())
                .cursos(material.getCursos() != null
                        ? material.getCursos().stream()
                                .map(c -> CursoResumenDTO.builder()
                                        .idCurso(c.getIdCurso())
                                        .nombre(c.getNombre())
                                        .build())
                                .collect(Collectors.toList())
                        : null)
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(MaterialRequestDTO dto, Material material) {
        if (dto == null || material == null) {
            return;
        }
        material.setTitulo(dto.getTitulo());
        material.setDescripcion(dto.getDescripcion());
        material.setTipo(dto.getTipo());
        material.setUrlArchivo(dto.getUrlArchivo());
        material.setFechaPublicacion(dto.getFechaPublicacion());
    }
}