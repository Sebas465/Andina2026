package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;

public class AulaDTOInsert {
    private Long idAula;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 80, message = "nombre admite como máximo 80 caracteres")
    private String nombre;

    @NotBlank(message = "seccion es obligatorio")
    @Size(max = 20, message = "seccion admite como máximo 20 caracteres")
    private String seccion;

    @NotNull(message = "capacidad es obligatorio")
    @Positive(message = "capacidad debe ser mayor que cero")
    private Integer capacidad;

    @NotNull(message = "idColegio es obligatorio")
    private Long idColegio;

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }
}
