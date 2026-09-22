package org.example.andina2026.dtos;

import java.time.LocalDateTime;

/** H1.1: escuela sin actividad reciente (ultimaActividad = null → nunca tuvo actividad registrada). */
public class EscuelaInactivaDTO {
    private Long idColegio;
    private String colegio;
    private String codigoModular;
    private LocalDateTime ultimaActividad;
    private Long diasSinActividad;

    public EscuelaInactivaDTO(Long idColegio, String colegio, String codigoModular, LocalDateTime ultimaActividad,
                              Long diasSinActividad) {
        this.idColegio = idColegio;
        this.colegio = colegio;
        this.codigoModular = codigoModular;
        this.ultimaActividad = ultimaActividad;
        this.diasSinActividad = diasSinActividad;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public String getColegio() {
        return colegio;
    }

    public String getCodigoModular() {
        return codigoModular;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public Long getDiasSinActividad() {
        return diasSinActividad;
    }
}
