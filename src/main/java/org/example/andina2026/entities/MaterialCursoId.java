package org.example.andina2026.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/** Clave compuesta de MATERIAL_CURSO (id_material + id_curso), como en el ERD. */
@Embeddable
public class MaterialCursoId implements Serializable {
    @Column(name = "id_material")
    private Long idMaterial;

    @Column(name = "id_curso")
    private Long idCurso;

    public MaterialCursoId() {
    }

    public MaterialCursoId(Long idMaterial, Long idCurso) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaterialCursoId that)) return false;
        return Objects.equals(idMaterial, that.idMaterial) && Objects.equals(idCurso, that.idCurso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaterial, idCurso);
    }
}
