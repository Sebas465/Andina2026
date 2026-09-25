package org.example.andina2026.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PersonaDTOInsert {
    private Long idPersona;

    // lo genera el sistema (no se envía al crear)
    private String codigoEstudiante;

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

    @Size(max = 10, message = "lenguaMaterna admite como máximo 10 caracteres")
    @Pattern(regexp = "QUECHUA|CASTELLANO|AMBOS", message = "lenguaMaterna debe ser QUECHUA, CASTELLANO o AMBOS")
    private String lenguaMaterna;

    @Size(max = 20, message = "estado admite como máximo 20 caracteres")
    private String estado;

    // H2.1: con el DNI se inicia sesión
    @Pattern(regexp = "\\d{8}", message = "dni debe tener 8 dígitos")
    private String dni;

    // Solo de entrada (nunca se devuelve) y solo la puede poner el ADMIN. Vacía = se conserva la actual.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 72, message = "password debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
            message = "password debe tener al menos una mayúscula, un número y un símbolo")
    private String password;

    // cuenta habilitada (solo ADMIN); vacío = true al crear, se conserva al modificar
    private Boolean enabled;

    // obligatorio para alumnos; el ADMIN y los especialistas no tienen aula
    private Long idAula;

    @NotNull(message = "idRol es obligatorio")
    private Long idRol;

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
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

    public String getLenguaMaterna() {
        return lenguaMaterna;
    }

    public void setLenguaMaterna(String lenguaMaterna) {
        this.lenguaMaterna = lenguaMaterna;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
