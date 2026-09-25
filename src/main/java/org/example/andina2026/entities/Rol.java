package org.example.andina2026.entities;

import jakarta.persistence.*;

/** Tabla «Rol» del ERD: tipo de persona (alumno, docente…). No confundir con Role (seguridad). */
@Entity
@Table(name = "roles_persona")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_persona")
    private Long idTipoPersona;

    @Column(name = "detalle", length = 100, nullable = false, unique = true)
    private String detalle;

    public Rol() {
    }

    public Long getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(Long idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
//Commit