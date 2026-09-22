package org.example.andina2026.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "asignaciones_docentes")
public class AsignacionDocente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long idAsignacion;

    @Column(name = "modalidad", length = 30)
    private String modalidad;

    @Column(name = "horas_semanales", precision = 4, scale = 1)
    private BigDecimal horasSemanales;

    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula aula;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoAcademico periodo;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_colegio", nullable = false)
    private Colegio colegio;

    public AsignacionDocente() {
    }

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

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public PeriodoAcademico getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoAcademico periodo) {
        this.periodo = periodo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }
}
