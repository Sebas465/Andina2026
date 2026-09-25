package org.example.andina2026.dtos;

import java.math.BigDecimal;


public class AulaDTOList {
    private Long idAula;

    private String nombre;

    private String seccion;

    private int capacidad;

    private Integer computadoras;

    private Integer proyectores;

    private BigDecimal conexionMbps;

    private Long idColegio;


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

}
