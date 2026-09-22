package org.example.andina2026.dtos;

import java.math.BigDecimal;

public class OcupacionAulaDTO {
    private Long idAula;
    private String aula;
    private String seccion;
    private String colegio;
    private Integer capacidad;
    private Long alumnos;
    private BigDecimal porcentajeOcupacion;

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Long getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(Long alumnos) {
        this.alumnos = alumnos;
    }

    public BigDecimal getPorcentajeOcupacion() {
        return porcentajeOcupacion;
    }

    public void setPorcentajeOcupacion(BigDecimal porcentajeOcupacion) {
        this.porcentajeOcupacion = porcentajeOcupacion;
    }
}
