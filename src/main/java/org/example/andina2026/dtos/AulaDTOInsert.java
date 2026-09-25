package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class AulaDTOInsert {
    private Long idAula;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 80, message = "nombre admite como máximo 80 caracteres")
    private String nombre;

    @NotBlank(message = "seccion es obligatorio")
    @Size(max = 20, message = "seccion admite como máximo 20 caracteres")
    private String seccion;

    @Positive(message = "capacidad debe ser mayor que cero")
    @Max(value = 40, message = "capacidad máxima de un aula: 40")
    private int capacidad;

    @PositiveOrZero(message = "computadoras no puede ser negativo")
    private Integer computadoras;

    @PositiveOrZero(message = "proyectores no puede ser negativo")
    private Integer proyectores;

    @DecimalMin(value = "0.0", message = "conexionMbps no puede ser negativa")
    private BigDecimal conexionMbps;

    @NotNull(message = "idColegio es obligatorio")
    private Long idColegio;

    private List<Long> idGrados = new ArrayList<>();

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getComputadoras() {
        return computadoras;
    }

    public void setComputadoras(Integer computadoras) {
        this.computadoras = computadoras;
    }

    public Integer getProyectores() {
        return proyectores;
    }

    public void setProyectores(Integer proyectores) {
        this.proyectores = proyectores;
    }

    public BigDecimal getConexionMbps() {
        return conexionMbps;
    }

    public void setConexionMbps(BigDecimal conexionMbps) {
        this.conexionMbps = conexionMbps;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public List<Long> getIdGrados() {
        return idGrados;
    }

    public void setIdGrados(List<Long> idGrados) {
        this.idGrados = idGrados;
    }
}
