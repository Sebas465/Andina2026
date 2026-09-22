package org.example.andina2026.dtos;

/** Alumnos matriculados por colegio y periodo. */
public class MatriculaColegioDTO {
    private Long idColegio;
    private String colegio;
    private String periodo;
    private Long alumnosMatriculados;

    public MatriculaColegioDTO(Long idColegio, String colegio, String periodo, Long alumnosMatriculados) {
        this.idColegio = idColegio;
        this.colegio = colegio;
        this.periodo = periodo;
        this.alumnosMatriculados = alumnosMatriculados;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public String getColegio() {
        return colegio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public Long getAlumnosMatriculados() {
        return alumnosMatriculados;
    }
}
