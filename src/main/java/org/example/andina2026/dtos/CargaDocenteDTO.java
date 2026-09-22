package org.example.andina2026.dtos;

import java.math.BigDecimal;

/** Carga lectiva de cada docente por periodo. */
public class CargaDocenteDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String periodo;
    private Long cursos;
    private BigDecimal horasSemanales;

    public CargaDocenteDTO(Long idPersona, String nombres, String apellidos, String periodo, Long cursos, BigDecimal horasSemanales) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.periodo = periodo;
        this.cursos = cursos;
        this.horasSemanales = horasSemanales;
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

    public String getPeriodo() {
        return periodo;
    }

    public Long getCursos() {
        return cursos;
    }

    public BigDecimal getHorasSemanales() {
        return horasSemanales;
    }
}
