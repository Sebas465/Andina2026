package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;

public class CursoDTOList {
    private String nombre_curso;

    private String descripcion;

    private String area;

    public String getNombre_curso() {
        return nombre_curso;
    }

    public void setNombre_curso(String nombre_curso) {
        this.nombre_curso = nombre_curso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
