package org.example.andina2026.dtos;

import java.math.BigDecimal;

/** Alumnos asignados frente a la capacidad del aula. */
public class OcupacionAulaDTO {
    private Long idAula;
    private String aula;
    private String seccion;
    private String colegio;
    private Integer capacidad;
    private Long alumnos;
    private BigDecimal porcentajeOcupacion;

    public OcupacionAulaDTO(Long idAula, String aula, String seccion, String colegio, Integer capacidad, Long alumnos, BigDecimal porcentajeOcupacion) {
        this.idAula = idAula;
        this.aula = aula;
        this.seccion = seccion;
        this.colegio = colegio;
        this.capacidad = capacidad;
        this.alumnos = alumnos;
        this.porcentajeOcupacion = porcentajeOcupacion;
    }

    public Long getIdAula() {
        return idAula;
    }

    public String getAula() {
        return aula;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getColegio() {
        return colegio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public Long getAlumnos() {
        return alumnos;
    }

    public BigDecimal getPorcentajeOcupacion() {
        return porcentajeOcupacion;
    }
}
