package org.example.andina2026.dtos;

import jakarta.validation.constraints.NotNull;

public class MaterialCursoDTO {
    @NotNull(message = "idMaterial es obligatorio")
    private Long idMaterial;

    @NotNull(message = "idCurso es obligatorio")
    private Long idCurso;

    public MaterialCursoDTO() {
    }

    public MaterialCursoDTO(Long idMaterial, Long idCurso) {
        this.idMaterial = idMaterial;
        this.idCurso = idCurso;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }
}
