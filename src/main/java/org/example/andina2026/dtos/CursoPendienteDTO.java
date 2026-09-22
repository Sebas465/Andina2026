package org.example.andina2026.dtos;

/** Curso al que le falta algo (docente o material). */
public class CursoPendienteDTO {
    private Long idCurso;
    private String curso;
    private String area;

    public CursoPendienteDTO(Long idCurso, String curso, String area) {
        this.idCurso = idCurso;
        this.curso = curso;
        this.area = area;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public String getCurso() {
        return curso;
    }

    public String getArea() {
        return area;
    }
}
