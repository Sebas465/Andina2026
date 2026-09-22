package org.example.andina2026;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Comprueba que cada consulta nativa de /api/reportes devuelve la respuesta correcta (con BD propia). */
// BD en memoria propia: estos datos no se mezclan con los de SeguridadYModeloTests
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:reportes;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportesDecisionTests {

    @Autowired
    MockMvc mvc;

    String admin, docente, psicologo, alumno;
    long colA, colB, aulaA, aulaB, rolAlumno, rolDocente, profe, periodo, mate, comu, arte, grado;

    private String login(String u, String p) throws Exception {
        String b = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + u + "\",\"password\":\"" + p + "\"}")).andReturn().getResponse().getContentAsString();
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(b);
        assertThat(m.find()).as("login de " + u).isTrue();
        return m.group(1);
    }

    private MockHttpServletRequestBuilder as(String t, MockHttpServletRequestBuilder r) {
        return r.header("Authorization", "Bearer " + t).contentType(MediaType.APPLICATION_JSON);
    }

    private long crear(String t, String url, String json) throws Exception {
        String loc = mvc.perform(as(t, post(url)).content(json)).andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        return Long.parseLong(loc.substring(loc.lastIndexOf('/') + 1));
    }

    private long alumno(String nombre, long aula, Double nota, String estadoPsico) throws Exception {
        long p = crear(admin, "/api/personas", "{\"nombres\":\"" + nombre + "\",\"apellidos\":\"Test\",\"idAula\":" + aula
                + ",\"idRol\":" + rolAlumno + "}");
        if (nota != null) {
            crear(admin, "/api/perfiles-academicos", "{\"notas\":" + nota + ",\"idPersona\":" + p
                    + (estadoPsico != null ? ",\"estadoPsicologico\":\"" + estadoPsico + "\"" : "") + "}");
        }
        return p;
    }

    @BeforeAll
    void datos() throws Exception {
        admin = login("admin", "AdminPrueba2026");
        crear(admin, "/api/usuarios", "{\"username\":\"doc\",\"password\":\"Docente2026!\",\"roles\":[\"DOCENTE\"]}");
        crear(admin, "/api/usuarios", "{\"username\":\"psi\",\"password\":\"Psicologo26!\",\"roles\":[\"PSICOLOGO\"]}");
        crear(admin, "/api/usuarios", "{\"username\":\"alu\",\"password\":\"Alumno2026!\",\"roles\":[\"ALUMNO\"]}");
        docente = login("doc", "Docente2026!");
        psicologo = login("psi", "Psicologo26!");
        alumno = login("alu", "Alumno2026!");

        colA = crear(admin, "/api/colegios", "{\"nombre\":\"IE Rural\",\"departamento\":\"Puno\",\"provincia\":\"P\",\"distrito\":\"D\",\"comunidad\":\"C\",\"tipo_zona\":\"RURAL\"}");
        colB = crear(admin, "/api/colegios", "{\"nombre\":\"IE Urbana\",\"departamento\":\"Lima\",\"provincia\":\"P\",\"distrito\":\"D\",\"comunidad\":\"C\",\"tipo_zona\":\"URBANA\"}");
        aulaA = crear(admin, "/api/aula", "{\"nombre\":\"A1\",\"seccion\":\"A\",\"capacidad\":4,\"idColegio\":" + colA + "}");
        aulaB = crear(admin, "/api/aula", "{\"nombre\":\"B1\",\"seccion\":\"A\",\"capacidad\":30,\"idColegio\":" + colB + "}");
        rolAlumno = crear(admin, "/api/roles-persona", "{\"detalle\":\"ALUMNO\"}");
        rolDocente = crear(admin, "/api/roles-persona", "{\"detalle\":\"DOCENTE\"}");

        // 6 alumnos en el aula A1 (capacidad 4 → 150 %), notas bajas; 6 en B1, notas altas
        long[] ids = new long[12];
        double[] notas = {5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17};
        for (int i = 0; i < 12; i++) {
            String psico = (notas[i] == 8) ? "Duelo familiar reciente" : (notas[i] == 15 ? "Estrés leve" : null);
            ids[i] = alumno("Alumno" + (int) notas[i], i < 6 ? aulaA : aulaB, notas[i], psico);
        }
        alumno("SinNota", aulaB, null, null);   // sin perfil: no aparece en rankings

        profe = crear(admin, "/api/personas", "{\"nombres\":\"Profe\",\"apellidos\":\"Mamani\",\"idAula\":" + aulaA + ",\"idRol\":" + rolDocente + "}");
        periodo = crear(admin, "/api/periodos", "{\"nombre\":\"2026-I\",\"fechaInicio\":\"2026-03-01\",\"fechaFin\":\"2026-07-31\"}");
        grado = crear(admin, "/api/grados", "{\"nombre\":\"1ro\",\"nivel\":\"Secundaria\"}");
        mate = crear(admin, "/api/cursos", "{\"nombre\":\"Matemática\",\"area\":\"Ciencias\"}");
        comu = crear(admin, "/api/cursos", "{\"nombre\":\"Comunicación\",\"area\":\"Letras\"}");
        arte = crear(admin, "/api/cursos", "{\"nombre\":\"Arte\",\"area\":\"Artes\"}");
        crear(admin, "/api/asignaciones-docentes", "{\"horasSemanales\":4.0,\"idAula\":" + aulaA + ",\"idCurso\":" + mate
                + ",\"idPeriodo\":" + periodo + ",\"idPersona\":" + profe + ",\"idColegio\":" + colA + "}");
        crear(admin, "/api/asignaciones-docentes", "{\"horasSemanales\":3.5,\"idAula\":" + aulaA + ",\"idCurso\":" + comu
                + ",\"idPeriodo\":" + periodo + ",\"idPersona\":" + profe + ",\"idColegio\":" + colA + "}");
        long mat = crear(admin, "/api/materiales", "{\"titulo\":\"Guía\",\"idPersona\":" + profe + "}");
        mvc.perform(as(admin, post("/api/materiales-cursos")).content("{\"idMaterial\":" + mat + ",\"idCurso\":" + mate + "}"))
                .andExpect(status().isCreated());
        for (int i = 0; i < 2; i++) {  // 2 alumnos matriculados en IE Rural (uno con 2 cursos: cuenta una vez)
            long m = crear(admin, "/api/matriculas", "{\"idColegio\":" + colA + ",\"idPersona\":" + ids[i] + "}");
            crear(admin, "/api/detalles-matricula", "{\"idMatricula\":" + m + ",\"idCurso\":" + mate + ",\"idPeriodo\":" + periodo + ",\"idGrado\":" + grado + "}");
            if (i == 0) {
                crear(admin, "/api/detalles-matricula", "{\"idMatricula\":" + m + ",\"idCurso\":" + comu + ",\"idPeriodo\":" + periodo + ",\"idGrado\":" + grado + "}");
            }
        }
    }

    @Test
    void losDiezPeoresAlumnosEnOrden() throws Exception {
        mvc.perform(as(docente, get("/api/reportes/alumnos-menor-promedio")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].nombres").value("Alumno5"))
                .andExpect(jsonPath("$[0].promedio").value(5.0))
                .andExpect(jsonPath("$[0].colegio").value("IE Rural"))
                .andExpect(jsonPath("$[9].nombres").value("Alumno15"))
                .andExpect(jsonPath("$[*].nombres", not(hasItem("Profe"))))
                .andExpect(jsonPath("$[*].nombres", not(hasItem("SinNota"))));
        mvc.perform(as(docente, get("/api/reportes/alumnos-menor-promedio?limite=3"))).andExpect(jsonPath("$", hasSize(3)));
        // H6.2: exportación CSV con cabecera, 3 filas y el peor alumno primero
        String csv = mvc.perform(as(docente, get("/api/reportes/alumnos-menor-promedio/csv?limite=3")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("alumnos_refuerzo.csv")))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        String[] filas = csv.replace("\uFEFF", "").trim().split("\n");
        assertThat(filas).hasSize(4);
        assertThat(filas[0]).isEqualTo("prioridad,idPersona,nombres,apellidos,aula,colegio,promedio");
        assertThat(filas[1]).startsWith("1,").contains("\"Alumno5\"", "\"IE Rural\"");
        mvc.perform(as(alumno, get("/api/reportes/alumnos-menor-promedio/csv"))).andExpect(status().isForbidden());
        mvc.perform(as(docente, get("/api/reportes/alumnos-menor-promedio?limite=0"))).andExpect(status().isBadRequest());
        mvc.perform(as(alumno, get("/api/reportes/alumnos-menor-promedio"))).andExpect(status().isForbidden());
    }

    @Test
    void alumnosEnRiesgoSinTextoClinico() throws Exception {
        String body = mvc.perform(as(psicologo, get("/api/reportes/alumnos-en-riesgo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))                       // solo el 8 (el 15 aprueba)
                .andExpect(jsonPath("$[0].nombres").value("Alumno8"))
                .andExpect(jsonPath("$[0].tieneObservacionPsicologica").value(true))
                .andReturn().getResponse().getContentAsString();
        assertThat(body).doesNotContain("Duelo", "Estrés", "estadoPsicologico");
        mvc.perform(as(docente, get("/api/reportes/alumnos-en-riesgo"))).andExpect(status().isForbidden());
    }

    @Test
    void rendimientoPorColegio() throws Exception {
        // IE Rural: notas 5..10 → promedio 7.50, 6/6 desaprobados (100 %); IE Urbana: 12..17 → 14.50, 0 %
        mvc.perform(as(admin, get("/api/reportes/rendimiento-colegios")))
                .andExpect(jsonPath("$[0].colegio").value("IE Rural"))
                .andExpect(jsonPath("$[0].tipoZona").value("RURAL"))
                .andExpect(jsonPath("$[0].promedio").value(7.5))
                .andExpect(jsonPath("$[0].desaprobados").value(6))
                .andExpect(jsonPath("$[0].porcentajeDesaprobados").value(100.0))
                .andExpect(jsonPath("$[1].promedio").value(14.5))
                .andExpect(jsonPath("$[1].desaprobados").value(0));
    }

    @Test
    void ocupacionCargaYPendientes() throws Exception {
        mvc.perform(as(admin, get("/api/reportes/ocupacion-aulas")))
                .andExpect(jsonPath("$[0].aula").value("A1"))
                .andExpect(jsonPath("$[0].alumnos").value(6))               // el docente no cuenta como alumno
                .andExpect(jsonPath("$[0].porcentajeOcupacion").value(150.0));
        mvc.perform(as(admin, get("/api/reportes/carga-docente")))
                .andExpect(jsonPath("$[0].nombres").value("Profe"))
                .andExpect(jsonPath("$[0].cursos").value(2))
                .andExpect(jsonPath("$[0].horasSemanales").value(7.5));
        mvc.perform(as(admin, get("/api/reportes/cursos-sin-docente/" + periodo)))
                .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].curso").value("Arte"));
        mvc.perform(as(admin, get("/api/reportes/cursos-sin-docente/99999"))).andExpect(status().isNotFound());
        mvc.perform(as(docente, get("/api/reportes/cursos-sin-material")))
                .andExpect(jsonPath("$[*].curso", containsInAnyOrder("Arte", "Comunicación")));
        mvc.perform(as(admin, get("/api/reportes/matriculas-colegio-periodo")))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].colegio").value("IE Rural"))
                .andExpect(jsonPath("$[0].alumnosMatriculados").value(2));
        mvc.perform(as(docente, get("/api/reportes/carga-docente"))).andExpect(status().isForbidden());
    }
}
