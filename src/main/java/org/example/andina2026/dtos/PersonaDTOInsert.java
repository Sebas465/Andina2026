package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PersonaDTOInsert {
    private Long idPersona;

    @NotBlank(message = "nombres es obligatorio")
    @Size(max = 100, message = "nombres admite como máximo 100 caracteres")
    private String nombres;

    @NotBlank(message = "apellidos es obligatorio")
    @Size(max = 100, message = "apellidos admite como máximo 100 caracteres")
    private String apellidos;

    private LocalDate fechaNacimiento;

    @Size(max = 150, message = "correo admite como máximo 150 caracteres")
    @Email(message = "correo no es válido")
    private String correo;

    @Size(max = 20, message = "estado admite como máximo 20 caracteres")
    private String estado;

    @NotNull(message = "idAula es obligatorio")
    private Long idAula;

    @NotNull(message = "idRol es obligatorio")
    private Long idRol;

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
}
