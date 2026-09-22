package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PerfilAcademicoDTOInsert {
    private Long idPerfilAcademico;

    private String detalles;

    @DecimalMin(value = "0.0", message = "notas debe estar entre 0 y 20")
    @DecimalMax(value = "20.0", message = "notas debe estar entre 0 y 20")
    private BigDecimal notas;

    private String estadoPsicologico;

    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    public Long getIdPerfilAcademico() {
        return idPerfilAcademico;
    }

    public void setIdPerfilAcademico(Long idPerfilAcademico) {
        this.idPerfilAcademico = idPerfilAcademico;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getNotas() {
        return notas;
    }

    public void setNotas(BigDecimal notas) {
        this.notas = notas;
    }

    public String getEstadoPsicologico() {
        return estadoPsicologico;
    }

    public void setEstadoPsicologico(String estadoPsicologico) {
        this.estadoPsicologico = estadoPsicologico;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }
}
