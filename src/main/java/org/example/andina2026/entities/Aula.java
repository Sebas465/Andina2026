package org.example.andina2026.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "aulas")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula")
    private Long idAula;

    @Column(name = "nombre", length = 80, nullable = false)
    private String nombre;

    @Column(name = "seccion", length = 20, nullable = false)
    private String seccion;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @ManyToOne
    @JoinColumn(name = "id_colegio", nullable = false)
    private Colegio colegio;

    public Aula() {
    }

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

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }
}
