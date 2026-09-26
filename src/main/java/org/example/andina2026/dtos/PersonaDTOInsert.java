package org.example.andina2026.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PersonaDTOInsert {
    private Long idPersona;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombresPersona;
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidosPersona;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaNacimientoPersona;
    @NotBlank(message = "El email es oligatorio")
    private String emailPersona;
    @NotBlank(message = "El estado es obligatorio")
    private String estadoPersona;

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombresPersona() {
        return nombresPersona;
    }

    public void setNombresPersona(String nombresPersona) {
        this.nombresPersona = nombresPersona;
    }

    public String getApellidosPersona() {
        return apellidosPersona;
    }

    public void setApellidosPersona(String apellidosPersona) {
        this.apellidosPersona = apellidosPersona;
    }

    public LocalDate getFechaNacimientoPersona() {
        return fechaNacimientoPersona;
    }

    public void setFechaNacimientoPersona(LocalDate fechaNacimientoPersona) {
        this.fechaNacimientoPersona = fechaNacimientoPersona;
    }

    public String getEmailPersona() {
        return emailPersona;
    }

    public void setEmailPersona(String emailPersona) {
        this.emailPersona = emailPersona;
    }

    public String getEstadoPersona() {
        return estadoPersona;
    }

    public void setEstadoPersona(String estadoPersona) {
        this.estadoPersona = estadoPersona;
    }
}
