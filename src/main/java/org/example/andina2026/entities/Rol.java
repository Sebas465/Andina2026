package org.example.andina2026.entities;

import jakarta.persistence.*;
//ORM
@Entity
@Table(name = "Rol")

public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_TipoPersona;

    @Column(name = "detalleRol", length = 50, nullable = false)
    private Long detalle;

    public Rol(Long id_TipoPersona, Long detalle) {
        this.id_TipoPersona = id_TipoPersona;
        this.detalle = detalle;
    }

    public Rol() { }

    public Long getId_TipoPersona() {
        return id_TipoPersona;
    }

    public void setId_TipoPersona(Long id_TipoPersona) {
        this.id_TipoPersona = id_TipoPersona;
    }

    public Long getDetalle() {
        return detalle;
    }

    public void setDetalle(Long detalle) {
        this.detalle = detalle;
    }

}
