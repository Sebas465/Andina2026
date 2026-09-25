package org.example.andina2026.dtos;

import jakarta.validation.constraints.*;

import java.util.List;

/** Solo de entrada: la contraseña nunca se devuelve en ninguna respuesta. */
public class UsersDTOInsert {
    @NotBlank(message = "dni es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "dni debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "username es obligatorio")
    @Size(min = 3, max = 50, message = "username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "[a-zA-Z0-9._-]+", message = "username solo admite letras, números, punto, guion y guion bajo")
    private String username;

    @NotBlank(message = "password es obligatorio")
    @Size(min = 8, max = 72, message = "password debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
            message = "password debe tener al menos una mayúscula, un número y un símbolo")
    private String password;

    @NotEmpty(message = "roles es obligatorio")
    private List<@Pattern(regexp = "ADMIN|ADMIN_ESCUELA|ESPECIALISTA|LOCAL", message = "rol no válido") String> roles;

    private Boolean enabled = true;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
//Commit