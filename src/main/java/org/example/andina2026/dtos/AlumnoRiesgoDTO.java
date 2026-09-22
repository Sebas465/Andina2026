package org.example.andina2026.dtos;

import java.math.BigDecimal;

/** Alumno desaprobado con observación psicológica registrada (sin el texto clínico). */
public class AlumnoRiesgoDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String aula;
    private BigDecimal promedio;
    private Boolean tieneObservacionPsicologica;

    public AlumnoRiesgoDTO(Long idPersona, String nombres, String apellidos, String aula, BigDecimal promedio, Boolean tieneObservacionPsicologica) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.aula = aula;
        this.promedio = promedio;
        this.tieneObservacionPsicologica = tieneObservacionPsicologica;
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

    public BigDecimal getPromedio() {
        return promedio;
    }

    public Boolean getTieneObservacionPsicologica() {
        return tieneObservacionPsicologica;
    }
}
