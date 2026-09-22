package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;

public class ColegioDTOInsert {
    private Long idColegio;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 200, message = "nombre admite como máximo 200 caracteres")
    private String nombre;

    @NotBlank(message = "departamento es obligatorio")
    @Size(max = 100, message = "departamento admite como máximo 100 caracteres")
    private String departamento;

    @NotBlank(message = "provincia es obligatorio")
    @Size(max = 100, message = "provincia admite como máximo 100 caracteres")
    private String provincia;

    @NotBlank(message = "distrito es obligatorio")
    @Size(max = 100, message = "distrito admite como máximo 100 caracteres")
    private String distrito;

    @NotBlank(message = "comunidad es obligatorio")
    @Size(max = 150, message = "comunidad admite como máximo 150 caracteres")
    private String comunidad;

    @NotBlank(message = "tipo_zona es obligatorio")
    @Size(max = 30, message = "tipo_zona admite como máximo 30 caracteres")
    private String tipo_zona;

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    public String getTipo_zona() {
        return tipo_zona;
    }

    public void setTipo_zona(String tipo_zona) {
        this.tipo_zona = tipo_zona;
    }
}
