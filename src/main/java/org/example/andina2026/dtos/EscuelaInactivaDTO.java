package org.example.andina2026.dtos;

import java.time.LocalDateTime;

public class EscuelaInactivaDTO {
    private Long idColegio;
    private String colegio;
    private String codigoModular;
    private LocalDateTime ultimaActividad;
    private Long diasSinActividad;

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getCodigoModular() {
        return codigoModular;
    }

    public void setCodigoModular(String codigoModular) {
        this.codigoModular = codigoModular;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Long getDiasSinActividad() {
        return diasSinActividad;
    }

    public void setDiasSinActividad(Long diasSinActividad) {
        this.diasSinActividad = diasSinActividad;
    }
}
