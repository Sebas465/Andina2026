package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;

public class RolDTOInsert {
    private Long idTipoPersona;

    @NotBlank(message = "detalle es obligatorio")
    @Size(max = 100, message = "detalle admite como máximo 100 caracteres")
    private String detalle;

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
