package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;

public class CursoDTOInsert {
    private Long idCurso;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre admite como máximo 100 caracteres")
    private String nombre;

    private String descripcion;

    @Size(max = 100, message = "area admite como máximo 100 caracteres")
    private String area;

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
//Commit