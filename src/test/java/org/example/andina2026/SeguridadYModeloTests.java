package org.example.andina2026;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Recorre las 13 tablas del ERD por la API real (con JWT) y comprueba:
 * permisos por rol, validaciones y que las listas no devuelvan datos sensibles.
 * Usa H2 en modo PostgreSQL (src/test/resources/application.properties).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeguridadYModeloTests {

    @Autowired
    MockMvc mvc;

    static String admin, docente, psicologo, alumno;
    static long colegio, aula, rol, persona, grado, periodo, curso, matricula, material, perfil;

    // ------------------------------------------------------------------ utilidades

    private String login(String user, String pass) throws Exception {
        String body = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    private MockHttpServletRequestBuilder as(String token, MockHttpServletRequestBuilder req) {
        return req.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);
    }

    /** POST que debe dar 201; devuelve el id del Location. */
    private long crear(String token, String url, String json) throws Exception {
        MvcResult r = mvc.perform(as(token, post(url)).content(json))
                .andExpect(status().isCreated())
                .andReturn();
        String loc = r.getResponse().getHeader("Location");
        return Long.parseLong(loc.substring(loc.lastIndexOf('/') + 1));
    }

    private String obtener(String token, String url) throws Exception {
        return mvc.perform(as(token, get(url))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    // ---------------------------------------------------------------------- pruebas

    @Test
    @Order(1)
    void sinTokenNoSeEntraYLoginMaloDa401() throws Exception {
        mvc.perform(get("/api/colegios")).andExpect(status().isUnauthorized());
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    void adminCreaUsuariosYLaListaNoTieneContrasenas() throws Exception {
        admin = login("admin", "AdminPrueba2026");
        crear(admin, "/api/usuarios", "{\"username\":\"docente1\",\"password\":\"Docente2026\",\"roles\":[\"DOCENTE\"]}");
        crear(admin, "/api/usuarios", "{\"username\":\"psico1\",\"password\":\"Psico20266\",\"roles\":[\"PSICOLOGO\"]}");
        crear(admin, "/api/usuarios", "{\"username\":\"alumno1\",\"password\":\"Alumno2026\",\"roles\":[\"ALUMNO\"]}");
        // usuario repetido y rol inventado → 400
        mvc.perform(as(admin, post("/api/usuarios"))
                        .content("{\"username\":\"docente1\",\"password\":\"OtraClave2026\",\"roles\":[\"DOCENTE\"]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/usuarios"))
                        .content("{\"username\":\"hacker\",\"password\":\"OtraClave2026\",\"roles\":[\"SUPERUSER\"]}"))
                .andExpect(status().isBadRequest());

        String lista = obtener(admin, "/api/usuarios");
        assertThat(lista).contains("docente1", "psico1", "alumno1");
        assertThat(lista.toLowerCase()).doesNotContain("password", "\"$2a$", "$2b$");

        docente = login("docente1", "Docente2026");
        psicologo = login("psico1", "Psico20266");
        alumno = login("alumno1", "Alumno2026");
        // solo ADMIN gestiona usuarios
        mvc.perform(as(docente, get("/api/usuarios"))).andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void crudDeLas13TablasDelErd() throws Exception {
        colegio = crear(admin, "/api/colegios", "{\"nombre\":\"IE Andina 501\",\"departamento\":\"Cusco\",\"provincia\":\"Urubamba\"," +
                "\"distrito\":\"Ollantaytambo\",\"comunidad\":\"Patacancha\",\"tipo_zona\":\"RURAL\"}");
        aula = crear(admin, "/api/aula", "{\"nombre\":\"Aula 1\",\"seccion\":\"A\",\"capacidad\":30,\"idColegio\":" + colegio + "}");
        rol = crear(admin, "/api/roles-persona", "{\"detalle\":\"ALUMNO\"}");
        persona = crear(admin, "/api/personas", "{\"nombres\":\"Rosa\",\"apellidos\":\"Quispe\",\"fechaNacimiento\":\"2014-05-02\"," +
                "\"correo\":\"rosa@andina.pe\",\"estado\":\"ACTIVO\",\"idAula\":" + aula + ",\"idRol\":" + rol + "}");
        grado = crear(admin, "/api/grados", "{\"nombre\":\"5to\",\"nivel\":\"Primaria\"}");
        periodo = crear(admin, "/api/periodos", "{\"nombre\":\"2026-I\",\"fechaInicio\":\"2026-03-01\",\"fechaFin\":\"2026-07-31\",\"estado\":\"ABIERTO\"}");
        curso = crear(admin, "/api/cursos", "{\"nombre\":\"Matemática\",\"descripcion\":\"Números\",\"area\":\"Ciencias\"}");
        matricula = crear(admin, "/api/matriculas", "{\"idColegio\":" + colegio + ",\"idPersona\":" + persona + "}");
        crear(admin, "/api/detalles-matricula", "{\"fechaMatricula\":\"2026-03-01\",\"estado\":\"VIGENTE\",\"idMatricula\":" + matricula +
                ",\"idCurso\":" + curso + ",\"idPeriodo\":" + periodo + ",\"idGrado\":" + grado + "}");
        crear(admin, "/api/asignaciones-docentes", "{\"modalidad\":\"PRESENCIAL\",\"horasSemanales\":4.5,\"idAula\":" + aula +
                ",\"idCurso\":" + curso + ",\"idPeriodo\":" + periodo + ",\"idPersona\":" + persona + ",\"idColegio\":" + colegio + "}");
        material = crear(docente, "/api/materiales", "{\"titulo\":\"Fracciones\",\"tipo\":\"PDF\",\"urlArchivo\":\"https://andina.pe/f.pdf\"," +
                "\"fechaPublicacion\":\"2026-03-10\",\"idPersona\":" + persona + "}");
        mvc.perform(as(docente, post("/api/materiales-cursos")).content("{\"idMaterial\":" + material + ",\"idCurso\":" + curso + "}"))
                .andExpect(status().isCreated());
        perfil = crear(psicologo, "/api/perfiles-academicos", "{\"detalles\":\"Participa en clase\",\"notas\":15.5," +
                "\"estadoPsicologico\":\"Ansiedad leve en evaluaciones\",\"idPersona\":" + persona + "}");

        // cada lista responde y trae lo creado
        for (String url : new String[]{"/api/colegios", "/api/aula", "/api/roles-persona", "/api/grados", "/api/periodos",
                "/api/cursos", "/api/asignaciones-docentes", "/api/materiales", "/api/materiales-cursos"}) {
            assertThat(obtener(alumno, url)).startsWith("[{");
        }
        assertThat(obtener(docente, "/api/matriculas")).contains("\"idPersona\":" + persona);
        assertThat(obtener(docente, "/api/detalles-matricula")).contains("\"idGrado\":" + grado);
        assertThat(obtener(admin, "/api/aula/" + aula)).contains("\"idColegio\":" + colegio);

        // actualizar y borrar
        mvc.perform(as(admin, put("/api/grados/" + grado)).content("{\"nombre\":\"5to grado\",\"nivel\":\"Primaria\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("5to grado"));
        long g2 = crear(admin, "/api/grados", "{\"nombre\":\"6to\",\"nivel\":\"Primaria\"}");
        mvc.perform(as(admin, delete("/api/grados/" + g2))).andExpect(status().isNoContent());
        mvc.perform(as(admin, get("/api/grados/" + g2))).andExpect(status().isNotFound());
        mvc.perform(as(docente, delete("/api/materiales-cursos/" + material + "/" + curso))).andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    void listasSinDatosSensibles() throws Exception {
        String personas = obtener(docente, "/api/personas");
        assertThat(personas).contains("Rosa").doesNotContain("rosa@andina.pe", "2014-05-02", "correo", "fechaNacimiento");
        // el detalle completo solo lo ve ADMIN
        assertThat(obtener(admin, "/api/personas/" + persona)).contains("rosa@andina.pe");
        mvc.perform(as(docente, get("/api/personas/" + persona))).andExpect(status().isForbidden());

        String perfiles = obtener(docente, "/api/perfiles-academicos");
        assertThat(perfiles).contains("Participa en clase")
                .doesNotContain("15.5", "Ansiedad", "notas", "estadoPsicologico");
        mvc.perform(as(docente, get("/api/perfiles-academicos/" + perfil))).andExpect(status().isForbidden());
        assertThat(obtener(psicologo, "/api/perfiles-academicos/" + perfil)).contains("Ansiedad leve");
        mvc.perform(as(alumno, get("/api/perfiles-academicos"))).andExpect(status().isForbidden());
        mvc.perform(as(alumno, get("/api/personas"))).andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void permisosPorRol() throws Exception {
        mvc.perform(as(alumno, post("/api/colegios")).content("{\"nombre\":\"X\",\"departamento\":\"a\",\"provincia\":\"b\"," +
                "\"distrito\":\"c\",\"comunidad\":\"d\",\"tipo_zona\":\"e\"}")).andExpect(status().isForbidden());
        mvc.perform(as(docente, delete("/api/colegios/" + colegio))).andExpect(status().isForbidden());
        mvc.perform(as(alumno, post("/api/materiales")).content("{\"titulo\":\"x\",\"idPersona\":" + persona + "}"))
                .andExpect(status().isForbidden());
        mvc.perform(as(alumno, get("/api/matriculas"))).andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    void validacionesYErrores() throws Exception {
        // capacidad negativa, campos obligatorios, correo y fechas incoherentes → 400
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"B\",\"seccion\":\"B\",\"capacidad\":-3,\"idColegio\":" + colegio + "}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/cursos")).content("{\"descripcion\":\"sin nombre\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/personas")).content("{\"nombres\":\"A\",\"apellidos\":\"B\",\"correo\":\"no-es-correo\"," +
                "\"idAula\":" + aula + ",\"idRol\":" + rol + "}")).andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/periodos")).content("{\"nombre\":\"mal\",\"fechaInicio\":\"2026-08-01\",\"fechaFin\":\"2026-03-01\"}"))
                .andExpect(status().isBadRequest());
        // relación inexistente → 404
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"C\",\"seccion\":\"C\",\"capacidad\":10,\"idColegio\":99999}"))
                .andExpect(status().isNotFound());
        // grado duplicado (nombre único) → 409
        mvc.perform(as(admin, post("/api/grados")).content("{\"nombre\":\"5to grado\",\"nivel\":\"Primaria\"}"))
                .andExpect(status().isConflict());
        // borrar un colegio con aulas → 409, sin exponer detalles de la BD
        mvc.perform(as(admin, delete("/api/colegios/" + colegio)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El registro está duplicado o tiene datos relacionados"));
    }
}
