package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class DetalleMatriculaDTOInsert {
    private Long idDetalleMatricula;

    private LocalDate fechaMatricula;

    @Size(max = 20, message = "estado admite como máximo 20 caracteres")
    private String estado;

    @NotNull(message = "idMatricula es obligatorio")
    private Long idMatricula;

    @NotNull(message = "idCurso es obligatorio")
    private Long idCurso;

    @NotNull(message = "idPeriodo es obligatorio")
    private Long idPeriodo;

    @NotNull(message = "idGrado es obligatorio")
    private Long idGrado;

    public Long getIdDetalleMatricula() {
        return idDetalleMatricula;
    }

    public void setIdDetalleMatricula(Long idDetalleMatricula) {
        this.idDetalleMatricula = idDetalleMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Long getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(Long idGrado) {
        this.idGrado = idGrado;
    }
}
//Commit