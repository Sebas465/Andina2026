package org.example.andina2026.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "perfiles_academicos")
public class PerfilAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil_academico")
    private Long idPerfilAcademico;

    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;

    @Column(name = "notas", precision = 4, scale = 2)
    private BigDecimal notas;

    @Column(name = "estado_psicologico", columnDefinition = "TEXT")
    private String estadoPsicologico;

    @OneToOne
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    public PerfilAcademico() {
    }

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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}
