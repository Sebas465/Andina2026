package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class AsignacionDTOList {
    private String id_aula;
    private Long curso;
    private String modalidad;
    private Long horassemanales;

    public String getId_aula() {
        return id_aula;
    }

    public void setId_aula(String id_aula) {
        this.id_aula = id_aula;
    }

    public Long getCurso() {
        return curso;
    }

    public void setCurso(Long curso) {
        this.curso = curso;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Long getHorassemanales() {
        return horassemanales;
    }

    public void setHorassemanales(Long horas_semanales) {
        this.horassemanales = horassemanales;
    }
}
