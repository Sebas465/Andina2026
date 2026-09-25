package org.example.andina2026.dtos;

import java.math.BigDecimal;

public class CargaDocenteDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String periodo;
    private Long cursos;
    private BigDecimal horasSemanales;

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

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getCursos() {
        return cursos;
    }

    public void setCursos(Long cursos) {
        this.cursos = cursos;
    }

    public BigDecimal getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(BigDecimal horasSemanales) {
        this.horasSemanales = horasSemanales;
    }
}
