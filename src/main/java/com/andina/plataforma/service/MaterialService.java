package com.andina.plataforma.service;

import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;

import java.util.List;

public interface MaterialService {
    MaterialResponseDTO crear(MaterialRequestDTO dto);
    MaterialResponseDTO obtenerPorId(Integer id);
    List<MaterialResponseDTO> obtenerTodos();
    List<MaterialResponseDTO> obtenerPorPersona(Integer idPersona);
    MaterialResponseDTO actualizar(Integer id, MaterialRequestDTO dto);
    void eliminar(Integer id);
    MaterialResponseDTO agregarCursoAMaterial(Integer idMaterial, Integer idCurso);
    MaterialResponseDTO quitarCursoDeMaterial(Integer idMaterial, Integer idCurso);
}