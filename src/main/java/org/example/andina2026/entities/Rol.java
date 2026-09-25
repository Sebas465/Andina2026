package org.example.andina2026.entities;

import jakarta.persistence.*;

/**
 * Tabla «Tipo_Persona» del ERD (alumno, docente, LOCAL, ESPECIALISTA…).
 * También es el rol de seguridad: una persona con cuenta recibe ROLE_ + detalle (ver JwtUserDetailsService).
 */
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

    /** Nombre del rol de seguridad: "LOCAL" → "ROLE_LOCAL", "Coordinador académico" → "ROLE_COORDINADOR_ACADÉMICO". */
    public String getAuthority() {
        return "ROLE_" + detalle.trim().toUpperCase().replaceAll("\\s+", "_");
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
