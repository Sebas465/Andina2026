package org.example.andina2026.dtos;

/** Para listas: sin datos sensibles. */
public class PerfilAcademicoDTOList {
    private Long idPerfilAcademico;

    private String detalles;

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

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }
}
