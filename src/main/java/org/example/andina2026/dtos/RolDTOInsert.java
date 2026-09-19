package org.example.andina2026.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;

public class RolDTOInsert {

    private Long id_TipoPersona;
    @NotEmpty(message = "El detalle es obligatorio")
    private Long detalle;

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
