package org.example.andina2026;

import org.example.andina2026.entities.Role;
import org.example.andina2026.entities.Users;
import org.example.andina2026.repositories.IUsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba autónoma de la capa de seguridad portada a master (solo usa la tabla de usuarios).
 * Valida el flujo real: 401 sin token, login con DNI + JWT, roles y HASHEO BCrypt de punta a punta.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadMasterTests {

    @Autowired MockMvc mvc;
    @Autowired IUsersRepository usersRepository;
    @Autowired PasswordEncoder passwordEncoder;

    /** Siembra un ADMIN con la contraseña YA hasheada, como haría el registro real. */
    @BeforeEach
    void seedAdmin() {
        usersRepository.deleteAll();
        Users admin = new Users();
        admin.setDni("00000001");
        admin.setUsername("adminMaster");
        admin.setPassword(passwordEncoder.encode("AdminMaster2026!"));
        admin.setEnabled(true);
        Role rol = new Role();
        rol.setRol("ROLE_ADMIN");
        rol.setUser(admin);
        admin.setRoles(List.of(rol));
        usersRepository.save(admin);
    }

    private String login(String dni, String pass) throws Exception {
        String body = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"" + dni + "\",\"password\":\"" + pass + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    @Test
    void sinTokenDa401() throws Exception {
        mvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginMaloDa401() throws Exception {
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"00000001\",\"password\":\"claveIncorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginBuenoDaTokenYAccesoAdmin() throws Exception {
        String token = login("00000001", "AdminMaster2026!");
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void elHasheoNoGuardaLaContraseñaEnClaro() {
        Users admin = usersRepository.findByDni("00000001").orElseThrow();
        assertThat(admin.getPassword()).isNotEqualTo("AdminMaster2026!");   // guardado como hash, no en claro
        assertThat(admin.getPassword()).startsWith("$2");                    // formato BCrypt
        assertThat(passwordEncoder.matches("AdminMaster2026!", admin.getPassword())).isTrue();
    }
}
