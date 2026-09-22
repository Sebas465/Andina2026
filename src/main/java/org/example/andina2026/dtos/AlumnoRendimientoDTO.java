package org.example.andina2026.dtos;

import java.math.BigDecimal;

/** Alumno con su promedio (0-20) para priorizar apoyo. */
public class AlumnoRendimientoDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String aula;
    private String colegio;
    private BigDecimal promedio;

    public AlumnoRendimientoDTO(Long idPersona, String nombres, String apellidos, String aula, String colegio, BigDecimal promedio) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.aula = aula;
        this.colegio = colegio;
        this.promedio = promedio;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getAula() {
        return aula;
    }

    public String getColegio() {
        return colegio;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }
}
