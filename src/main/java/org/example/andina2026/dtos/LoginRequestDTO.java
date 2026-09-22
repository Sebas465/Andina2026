package org.example.andina2026.dtos;

/** H2.1: inicio de sesión con DNI y contraseña. */
public class LoginRequestDTO {
    private String dni;
    private String password;

    public LoginRequestDTO() {
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
}
