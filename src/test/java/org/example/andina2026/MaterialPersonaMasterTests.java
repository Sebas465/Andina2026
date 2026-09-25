package org.example.andina2026;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.andina2026.entities.*;
import org.example.andina2026.repositories.IMaterialRepository;
import org.example.andina2026.repositories.IUsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba autónoma del feature Material + servicio de Persona portados a master.
 * Siembra la cadena mínima (Colegio->Aula, Rol, Persona) y ejercita la API real con JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaterialPersonaMasterTests {

    @Autowired MockMvc mvc;
    @Autowired IUsersRepository usersRepository;
    @Autowired IMaterialRepository materialRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @PersistenceContext EntityManager em;

    Long idPersona;

    @BeforeEach
    void seed() {
        // Cadena mínima que Persona exige: Colegio -> Aula, y Rol
        Colegio colegio = new Colegio();
        colegio.setNombre("IE de prueba");
        colegio.setDepartamento("Cusco");
        colegio.setProvincia("Cusco");
        colegio.setDistrito("Cusco");
        colegio.setComunidad("Comunidad");
        colegio.setTipo_zona("rural");
        em.persist(colegio);

        Aula aula = new Aula();
        aula.setNombre("Aula 1");
        aula.setSeccion("A");
        aula.setCapacidad(30);
        aula.setColegio(colegio);
        em.persist(aula);

        Rol rol = new Rol();
        rol.setDetalle("ALUMNO");
        em.persist(rol);

        Persona persona = new Persona();
        persona.setNombres("Ana");
        persona.setApellidos("Quispe");
        persona.setFechaNacimiento(LocalDate.of(2012, 5, 1));
        persona.setAula(aula);
        persona.setRol(rol);
        em.persist(persona);

        // Usuario ADMIN para obtener token (contraseña hasheada, como el registro real)
        Users admin = new Users();
        admin.setDni("00000002");
        admin.setUsername("adminMat");
        admin.setPassword(passwordEncoder.encode("AdminMat2026!"));
        admin.setEnabled(true);
        Role r = new Role();
        r.setRol("ROLE_ADMIN");
        r.setUser(admin);
        admin.setRoles(List.of(r));
        usersRepository.save(admin);

        em.flush();
        idPersona = persona.getIdPersona();
    }

    private String login() throws Exception {
        String body = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"00000002\",\"password\":\"AdminMat2026!\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    @Test
    void sinTokenDa401() throws Exception {
        mvc.perform(get("/api/materiales")).andExpect(status().isUnauthorized());
    }

    @Test
    void crearYListarMaterialConSuPersona() throws Exception {
        String token = login();
        String json = "{\"titulo\":\"Guía de matemáticas\",\"descripcion\":\"unidad 1\",\"tipo\":\"PDF\","
                + "\"urlArchivo\":\"http://x/y.pdf\",\"fechaPublicacion\":\"2026-09-25\",\"idPersona\":" + idPersona + "}";
        mvc.perform(post("/api/materiales").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMaterial").isNumber())
                .andExpect(jsonPath("$.titulo").value("Guía de matemáticas"))
                .andExpect(jsonPath("$.idPersona").value(idPersona.intValue()));

        mvc.perform(get("/api/materiales").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Guía de matemáticas"));

        assertThat(materialRepository.findAll()).hasSize(1);
        assertThat(materialRepository.findAll().get(0).getPersona().getIdPersona()).isEqualTo(idPersona);
    }
}
