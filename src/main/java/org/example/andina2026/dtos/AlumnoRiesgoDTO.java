package org.example.andina2026.dtos;

import java.math.BigDecimal;

public class AlumnoRiesgoDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String aula;
    private BigDecimal promedio;
    private Boolean tieneObservacionPsicologica;

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public void setPromedio(BigDecimal promedio) {
        this.promedio = promedio;
    }

    public Boolean getTieneObservacionPsicologica() {
        return tieneObservacionPsicologica;
    }

    public void setTieneObservacionPsicologica(Boolean tieneObservacionPsicologica) {
        this.tieneObservacionPsicologica = tieneObservacionPsicologica;
    }
}
