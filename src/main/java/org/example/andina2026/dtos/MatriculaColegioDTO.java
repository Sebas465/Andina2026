package org.example.andina2026.dtos;

public class MatriculaColegioDTO {
    private Long idColegio;
    private String colegio;
    private String periodo;
    private Long alumnosMatriculados;

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

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Long getAlumnosMatriculados() {
        return alumnosMatriculados;
    }

    public void setAlumnosMatriculados(Long alumnosMatriculados) {
        this.alumnosMatriculados = alumnosMatriculados;
    }
}
