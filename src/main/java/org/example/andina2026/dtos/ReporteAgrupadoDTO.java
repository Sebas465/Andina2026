package org.example.andina2026.dtos;

/** Reporte de "nombre + cantidad": ocupación de aulas, carga docente y matrículas por colegio. */
public class ReporteAgrupadoDTO {
    private String categoria;
    private Integer cantidad;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
