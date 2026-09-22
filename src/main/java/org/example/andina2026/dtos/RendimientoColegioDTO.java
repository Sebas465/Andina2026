package org.example.andina2026.dtos;

import java.math.BigDecimal;

/** Rendimiento agregado por colegio. */
public class RendimientoColegioDTO {
    private Long idColegio;
    private String colegio;
    private String tipoZona;
    private Long alumnosEvaluados;
    private BigDecimal promedio;
    private Long desaprobados;
    private BigDecimal porcentajeDesaprobados;

    public RendimientoColegioDTO(Long idColegio, String colegio, String tipoZona, Long alumnosEvaluados, BigDecimal promedio, Long desaprobados, BigDecimal porcentajeDesaprobados) {
        this.idColegio = idColegio;
        this.colegio = colegio;
        this.tipoZona = tipoZona;
        this.alumnosEvaluados = alumnosEvaluados;
        this.promedio = promedio;
        this.desaprobados = desaprobados;
        this.porcentajeDesaprobados = porcentajeDesaprobados;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public String getColegio() {
        return colegio;
    }

    public String getTipoZona() {
        return tipoZona;
    }

    public Long getAlumnosEvaluados() {
        return alumnosEvaluados;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public Long getDesaprobados() {
        return desaprobados;
    }

    public BigDecimal getPorcentajeDesaprobados() {
        return porcentajeDesaprobados;
    }
}
