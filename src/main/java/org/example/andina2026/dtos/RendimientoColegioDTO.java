package org.example.andina2026.dtos;

import java.math.BigDecimal;

public class RendimientoColegioDTO {
    private Long idColegio;
    private String colegio;
    private String tipoZona;
    private Long alumnosEvaluados;
    private BigDecimal promedio;
    private Long desaprobados;
    private BigDecimal porcentajeDesaprobados;

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(String tipoZona) {
        this.tipoZona = tipoZona;
    }

    public Long getAlumnosEvaluados() {
        return alumnosEvaluados;
    }

    public void setAlumnosEvaluados(Long alumnosEvaluados) {
        this.alumnosEvaluados = alumnosEvaluados;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public void setPromedio(BigDecimal promedio) {
        this.promedio = promedio;
    }

    public Long getDesaprobados() {
        return desaprobados;
    }

    public void setDesaprobados(Long desaprobados) {
        this.desaprobados = desaprobados;
    }

    public BigDecimal getPorcentajeDesaprobados() {
        return porcentajeDesaprobados;
    }

    public void setPorcentajeDesaprobados(BigDecimal porcentajeDesaprobados) {
        this.porcentajeDesaprobados = porcentajeDesaprobados;
    }
}
