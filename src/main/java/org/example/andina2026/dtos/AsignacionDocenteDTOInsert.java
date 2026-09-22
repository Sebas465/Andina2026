package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AsignacionDocenteDTOInsert {
    private Long idAsignacion;

    @Size(max = 30, message = "modalidad admite como máximo 30 caracteres")
    private String modalidad;

    @DecimalMin(value = "0.0", message = "horasSemanales no puede ser negativo")
    @Digits(integer = 3, fraction = 1, message = "horasSemanales admite 3 enteros y 1 decimal")
    private BigDecimal horasSemanales;

    @NotNull(message = "idAula es obligatorio")
    private Long idAula;

    @NotNull(message = "idCurso es obligatorio")
    private Long idCurso;

    @NotNull(message = "idPeriodo es obligatorio")
    private Long idPeriodo;

    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    @NotNull(message = "idColegio es obligatorio")
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
