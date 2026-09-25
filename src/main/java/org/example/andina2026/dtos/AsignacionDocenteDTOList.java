package org.example.andina2026.dtos;

import java.math.BigDecimal;

public class AsignacionDocenteDTOList {
    private Long idAsignacion;

    private String modalidad;

    private BigDecimal horasSemanales;

    private Long idAula;

    private Long idCurso;

    private Long idPeriodo;

    private Long idPersona;

    private Long idColegio;

    public Long getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Long idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public BigDecimal getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(BigDecimal horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }
}
//Commit