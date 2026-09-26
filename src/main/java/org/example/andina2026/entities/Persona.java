package org.example.andina2026.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(name="nombresPersona" ,length = 150, nullable = false)
    private String nombresPersona;

    @Column(name="apellidosPersona" ,length = 150, nullable = false)
    private String apellidosPersona;

    @Column(name="fechaNacimientoPersona" , nullable = false)
    private LocalDate fechaNacimientoPersona;

    @Column(name="emailPersona" ,length = 150, nullable = false)
    private String emailPersona;

    @Column(name="estadoPersona" ,length = 100, nullable = false)
    private String estadoPersona;

    @ManyToOne
    @JoinColumn(name = "idTipoPersona", nullable = false)
    private Rol rol;

    public Persona() {
    }

    public Persona(Long idPersona, String nombresPersona, String apellidosPersona, LocalDate fechaNacimientoPersona, String emailPersona, String estadoPersona) {
        this.idPersona = idPersona;
        this.nombresPersona = nombresPersona;
        this.apellidosPersona = apellidosPersona;
        this.fechaNacimientoPersona = fechaNacimientoPersona;
        this.emailPersona = emailPersona;
        this.estadoPersona = estadoPersona;
    }

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
