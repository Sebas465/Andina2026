package org.example.andina2026.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "personas")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @Column(name = "codigo_estudiante", length = 12, unique = true)
    private String codigoEstudiante;

    @Column(name = "nombres", length = 100, nullable = false)
    private String nombres;

    @Column(name = "apellidos", length = 100, nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "correo", length = 150, unique = true)
    private String correo;

    @Column(name = "lengua_materna", length = 10)
    private String lenguaMaterna;

    @Column(name = "estado", length = 20)
    private String estado;

    // Cuenta de acceso (antes tabla «users»): se inicia sesión con el DNI (H2.1)
    @Column(name = "dni", length = 8, unique = true)
    private String dni;

    // hash BCrypt; null = la persona no tiene cuenta (p. ej. un alumno)
    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    // opcional: el ADMIN del sistema y los especialistas no pertenecen a un aula
    @ManyToOne
    @JoinColumn(name = "id_aula")
    private Aula aula;

    // Tipo_Persona: también es el rol de seguridad (LOCAL → ROLE_LOCAL)
    @ManyToOne
    @JoinColumn(name = "id_tipo_persona", nullable = false)
    private Rol rol;

    public Persona() {
    }

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

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
