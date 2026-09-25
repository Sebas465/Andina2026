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

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Recorre las 13 tablas del ERD por la API real (con JWT) y comprueba lo que pide el Word:
 * login con DNI y roles ESPECIALISTA/LOCAL/ADMIN_ESCUELA (H2.1), escuelas con código modular e historial (H1.1),
 * aulas con equipamiento y capacidad (H1.2), matrícula con lengua materna, edad 12-16 e ID anonimizado (H2.2),
 * y que las listas no devuelvan datos sensibles. Usa H2 en modo PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeguridadYModeloTests {

    @Autowired
    MockMvc mvc;

    static String admin, especialista, local, adminEscuela;
    static long colegio, aula, rolAlumno, persona, grado, periodo, curso, matricula, material, perfil;
    static String codigoEstudiante;

    /** Fecha de nacimiento de alguien que hoy tiene `edad` años. */
    static String nacido(int edad) {
        return LocalDate.now().minusYears(edad).minusDays(10).toString();
    }

    // ------------------------------------------------------------------ utilidades

    private String login(String dni, String pass) throws Exception {
        String body = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"" + dni + "\",\"password\":\"" + pass + "\"}"))
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

    /** Persona con cuenta (antes «usuario»): el tipo de persona es su rol. */
    private String cuenta(String dni, String nombre, String pass, long idTipo) {
        return "{\"nombres\":\"" + nombre + "\",\"apellidos\":\"Prueba\",\"dni\":\"" + dni + "\",\"password\":\"" + pass
                + "\",\"idRol\":" + idTipo + "}";
    }

    // ---------------------------------------------------------------------- pruebas

    @Test
    @Order(1)
    void sinTokenNoSeEntraYLoginMaloDa401() throws Exception {
        mvc.perform(get("/api/colegios")).andExpect(status().isUnauthorized());
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"00000001\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
        // el nombre de usuario ya no sirve para entrar: se entra con DNI (H2.1)
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"admin\",\"password\":\"AdminPrueba2026\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    void personasConCuentaDniTipoComoRolYContrasenaSegura() throws Exception {
        admin = login("00000001", "AdminPrueba2026");
        // el Tipo_Persona es el rol de seguridad (LOCAL → ROLE_LOCAL)
        long tipoEsp = crear(admin, "/api/roles-persona", "{\"detalle\":\"ESPECIALISTA\"}");
        long tipoLocal = crear(admin, "/api/roles-persona", "{\"detalle\":\"LOCAL\"}");
        long tipoDir = crear(admin, "/api/roles-persona", "{\"detalle\":\"ADMIN_ESCUELA\"}");
        long esp = crear(admin, "/api/personas", cuenta("41234567", "Esp", "Especial2026!", tipoEsp));
        crear(admin, "/api/personas", cuenta("42345678", "Local", "LocalAula2026!", tipoLocal));
        crear(admin, "/api/personas", cuenta("43456789", "Director", "Director2026!", tipoDir));
        // DNI repetido, DNI mal formado y contraseña débil → 400
        mvc.perform(as(admin, post("/api/personas")).content(cuenta("41234567", "Otro", "OtraClave2026!", tipoLocal)))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/personas")).content(cuenta("1234", "Corto", "OtraClave2026!", tipoLocal)))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/personas")).content(cuenta("45555555", "Debil", "solominusculas", tipoLocal)))
                .andExpect(status().isBadRequest());
        // el token dura 8 horas (exp - iat = 28800 s) y su sujeto es el DNI
        String payload = new String(java.util.Base64.getUrlDecoder().decode(admin.split("\\.")[1]));
        long iat = Long.parseLong(payload.replaceAll(".*\"iat\":(\\d+).*", "$1"));
        long exp = Long.parseLong(payload.replaceAll(".*\"exp\":(\\d+).*", "$1"));
        assertThat(exp - iat).isEqualTo(8 * 60 * 60);
        assertThat(payload).contains("\"sub\":\"00000001\"");

        // ni la lista ni la ficha devuelven la contraseña o su hash
        String ficha = obtener(admin, "/api/personas/" + esp);
        assertThat(ficha).contains("41234567");
        assertThat((obtener(admin, "/api/personas") + ficha).toLowerCase()).doesNotContain("password", "\"$2a$", "$2b$");

        especialista = login("41234567", "Especial2026!");
        local = login("42345678", "LocalAula2026!");
        adminEscuela = login("43456789", "Director2026!");
        // solo el ADMIN crea cuentas o asigna tipos con acceso: un LOCAL no puede darse permisos
        mvc.perform(as(local, post("/api/personas")).content(cuenta("46666666", "Colado", "Colado2026!!", tipoLocal)))
                .andExpect(status().isForbidden());
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"Sin\",\"apellidos\":\"Clave\",\"idRol\":" + tipoDir + "}"))
                .andExpect(status().isForbidden());
        mvc.perform(as(local, delete("/api/personas/" + esp))).andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void crudDeLas13TablasDelErd() throws Exception {
        colegio = crear(admin, "/api/colegios", "{\"codigoModular\":\"0501234\",\"nombre\":\"IE Andina 501\",\"departamento\":\"Cusco\"," +
                "\"provincia\":\"Urubamba\",\"distrito\":\"Ollantaytambo\",\"comunidad\":\"Patacancha\",\"tipo_zona\":\"RURAL\"}");
        grado = crear(admin, "/api/grados", "{\"nombre\":\"1° Secundaria\",\"nivel\":\"Secundaria\"}");
        aula = crear(admin, "/api/aula", "{\"nombre\":\"Aula 1\",\"seccion\":\"A\",\"capacidad\":2,\"computadoras\":10," +
                "\"proyectores\":1,\"conexionMbps\":3.5,\"idColegio\":" + colegio + "}");
        rolAlumno = crear(admin, "/api/roles-persona", "{\"detalle\":\"ALUMNO\"}");
        persona = crear(local, "/api/personas", "{\"nombres\":\"Rosa\",\"apellidos\":\"Quispe\",\"fechaNacimiento\":\"" + nacido(13) + "\"," +
                "\"correo\":\"rosa@andina.pe\",\"lenguaMaterna\":\"QUECHUA\",\"estado\":\"ACTIVO\",\"idAula\":" + aula + ",\"idRol\":" + rolAlumno + "}");
        periodo = crear(adminEscuela, "/api/periodos", "{\"nombre\":\"2026-I\",\"fechaInicio\":\"2026-03-01\",\"fechaFin\":\"2026-07-31\",\"estado\":\"ABIERTO\"}");
        curso = crear(adminEscuela, "/api/cursos", "{\"nombre\":\"Matemática\",\"descripcion\":\"Números\",\"area\":\"Ciencias\"}");
        matricula = crear(admin, "/api/matriculas", "{\"idColegio\":" + colegio + ",\"idPersona\":" + persona + "}");
        crear(admin, "/api/detalles-matricula", "{\"fechaMatricula\":\"2026-03-01\",\"estado\":\"VIGENTE\",\"idMatricula\":" + matricula +
                ",\"idCurso\":" + curso + ",\"idPeriodo\":" + periodo + ",\"idGrado\":" + grado + "}");
        crear(adminEscuela, "/api/asignaciones-docentes", "{\"modalidad\":\"PRESENCIAL\",\"horasSemanales\":4.5,\"idAula\":" + aula +
                ",\"idCurso\":" + curso + ",\"idPeriodo\":" + periodo + ",\"idPersona\":" + persona + ",\"idColegio\":" + colegio + "}");
        material = crear(especialista, "/api/materiales", "{\"titulo\":\"Fracciones\",\"tipo\":\"PDF\",\"urlArchivo\":\"https://andina.pe/f.pdf\"," +
                "\"fechaPublicacion\":\"2026-03-10\",\"idPersona\":" + persona + "}");
        mvc.perform(as(especialista, post("/api/materiales-cursos")).content("{\"idMaterial\":" + material + ",\"idCurso\":" + curso + "}"))
                .andExpect(status().isCreated());
        perfil = crear(local, "/api/perfiles-academicos", "{\"detalles\":\"Participa en clase\",\"notas\":15.5," +
                "\"estadoPsicologico\":\"Ansiedad leve en evaluaciones\",\"idPersona\":" + persona + "}");

        for (String url : new String[]{"/api/colegios", "/api/aula", "/api/roles-persona", "/api/grados", "/api/periodos",
                "/api/cursos", "/api/asignaciones-docentes", "/api/materiales", "/api/materiales-cursos"}) {
            assertThat(obtener(especialista, url)).startsWith("[{");
        }
        assertThat(obtener(especialista, "/api/matriculas")).contains("\"idPersona\":" + persona);
        assertThat(obtener(local, "/api/detalles-matricula")).contains("\"idGrado\":" + grado);

        // H1.2: el aula guarda su equipamiento tecnológico (el grado va en el detalle de matrícula, por periodo)
        mvc.perform(as(admin, get("/api/aula/" + aula)))
                .andExpect(jsonPath("$.idColegio").value(colegio))
                .andExpect(jsonPath("$.computadoras").value(10))
                .andExpect(jsonPath("$.conexionMbps").value(3.5));

        // actualizar y borrar
        mvc.perform(as(admin, put("/api/grados/" + grado)).content("{\"nombre\":\"1° Secundaria EBR\",\"nivel\":\"Secundaria\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("1° Secundaria EBR"));
        long g2 = crear(admin, "/api/grados", "{\"nombre\":\"2° Secundaria\",\"nivel\":\"Secundaria\"}");
        mvc.perform(as(admin, delete("/api/grados/" + g2))).andExpect(status().isNoContent());
        mvc.perform(as(admin, get("/api/grados/" + g2))).andExpect(status().isNotFound());
        mvc.perform(as(especialista, delete("/api/materiales-cursos/" + material + "/" + curso))).andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    void matriculaDelWord() throws Exception {
        // H2.2: ID anonimizado generado por el sistema, que no cambia al editar la ficha
        String ficha = obtener(local, "/api/personas/" + persona);
        codigoEstudiante = ficha.replaceAll(".*\"codigoEstudiante\":\"(EST-[0-9A-F]{8})\".*", "$1");
        assertThat(codigoEstudiante).matches("EST-[0-9A-F]{8}");
        mvc.perform(as(local, put("/api/personas/" + persona)).content("{\"codigoEstudiante\":\"EST-HACKEADO\",\"nombres\":\"Rosa María\"," +
                        "\"apellidos\":\"Quispe\",\"fechaNacimiento\":\"" + nacido(13) + "\",\"correo\":\"rosa@andina.pe\"," +
                        "\"lenguaMaterna\":\"AMBOS\",\"estado\":\"ACTIVO\",\"idAula\":" + aula + ",\"idRol\":" + rolAlumno + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoEstudiante").value(codigoEstudiante));
        // la lista muestra el ID anonimizado pero no los datos sensibles
        assertThat(obtener(especialista, "/api/personas")).contains(codigoEstudiante);

        // H2.2: edad 12-16, lengua materna obligatoria y válida para un alumno
        String base = "\"apellidos\":\"Test\",\"idAula\":" + aula + ",\"idRol\":" + rolAlumno;
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"Niño\",\"fechaNacimiento\":\"" + nacido(10) + "\",\"lenguaMaterna\":\"QUECHUA\"," + base + "}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", containsString("entre 12 y 16")));
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"Mayor\",\"fechaNacimiento\":\"" + nacido(17) + "\",\"lenguaMaterna\":\"QUECHUA\"," + base + "}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"SinLengua\",\"fechaNacimiento\":\"" + nacido(13) + "\"," + base + "}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", containsString("lenguaMaterna")));
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"Lengua\",\"fechaNacimiento\":\"" + nacido(13) + "\",\"lenguaMaterna\":\"INGLES\"," + base + "}"))
                .andExpect(status().isBadRequest());
        // H1.2: capacidad máxima 40 y el aula (capacidad 2) no admite un tercer alumno
        crear(local, "/api/personas", "{\"nombres\":\"Segundo\",\"fechaNacimiento\":\"" + nacido(14) + "\",\"lenguaMaterna\":\"CASTELLANO\"," + base + "}");
        mvc.perform(as(local, post("/api/personas")).content("{\"nombres\":\"Tercero\",\"fechaNacimiento\":\"" + nacido(14) + "\",\"lenguaMaterna\":\"CASTELLANO\"," + base + "}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", containsString("llena")));
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"Grande\",\"seccion\":\"Z\",\"capacidad\":41,\"idColegio\":" + colegio + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void historialDeCambios() throws Exception {
        // H1.1: historial de auditoría de la escuela (quién, qué acción, qué campos)
        mvc.perform(as(admin, put("/api/colegios/" + colegio)).content("{\"codigoModular\":\"0501234\",\"nombre\":\"IE Andina 501 Patacancha\"," +
                "\"departamento\":\"Cusco\",\"provincia\":\"Urubamba\",\"distrito\":\"Ollantaytambo\",\"comunidad\":\"Patacancha\",\"tipo_zona\":\"RURAL\"}"))
                .andExpect(status().isOk());
        mvc.perform(as(adminEscuela, get("/api/auditoria?entidad=Colegio&idRegistro=" + colegio)))
                .andExpect(jsonPath("$[0].accion").value("MODIFICAR"))
                .andExpect(jsonPath("$[0].detalle").value("Campos modificados: nombre"))
                .andExpect(jsonPath("$[0].usuario").value("00000001"))
                .andExpect(jsonPath("$[1].accion").value("CREAR"));
        // H2.2: historial de la ficha del estudiante, sin guardar valores sensibles
        String h = obtener(local, "/api/auditoria?entidad=Persona&idRegistro=" + persona);
        assertThat(h).contains("MODIFICAR", "nombres", "lenguaMaterna", "42345678").doesNotContain("Rosa María", "rosa@andina.pe");
        assertThat(obtener(especialista, "/api/auditoria?entidad=Persona&idRegistro=" + persona)).contains("MODIFICAR");
    }

    @Test
    @Order(6)
    void listasSinDatosSensibles() throws Exception {
        String personas = obtener(especialista, "/api/personas");
        assertThat(personas).contains("Rosa").doesNotContain("rosa@andina.pe", "correo", "fechaNacimiento");
        assertThat(obtener(local, "/api/personas/" + persona)).contains("rosa@andina.pe");
        assertThat(obtener(especialista, "/api/personas/" + persona)).contains("rosa@andina.pe"); // el especialista ve al alumno

        String perfiles = obtener(especialista, "/api/perfiles-academicos");
        assertThat(perfiles).contains("Participa en clase")
                .doesNotContain("15.5", "Ansiedad", "notas", "estadoPsicologico");
        assertThat(obtener(especialista, "/api/perfiles-academicos/" + perfil)).contains("15.5"); // y sus notas
        assertThat(obtener(local, "/api/perfiles-academicos/" + perfil)).contains("Ansiedad leve");
    }

    @Test
    @Order(7)
    void permisosPorRol() throws Exception {
        // Sin cuerpo (DELETE): así se prueba solo el permiso; con POST inválido Spring valida primero (400), igual que en la demo
        mvc.perform(as(especialista, delete("/api/colegios/" + colegio))).andExpect(status().isForbidden());
        mvc.perform(as(adminEscuela, delete("/api/colegios/" + colegio))).andExpect(status().isForbidden());
        mvc.perform(as(local, delete("/api/colegios/" + colegio))).andExpect(status().isForbidden());
        mvc.perform(as(especialista, delete("/api/personas/" + persona))).andExpect(status().isForbidden());
        mvc.perform(as(especialista, delete("/api/periodos/" + periodo))).andExpect(status().isForbidden());
        // Solo el ADMIN toca las matrículas (cuerpo válido: así el 403 viene del rol, no de la validación)
        String matriculaJson = "{\"idColegio\":" + colegio + ",\"idPersona\":" + persona + "}";
        mvc.perform(as(local, post("/api/matriculas")).content(matriculaJson)).andExpect(status().isForbidden());
        mvc.perform(as(adminEscuela, post("/api/matriculas")).content(matriculaJson)).andExpect(status().isForbidden());
        mvc.perform(as(especialista, post("/api/matriculas")).content(matriculaJson)).andExpect(status().isForbidden());
        mvc.perform(as(local, delete("/api/matriculas/" + matricula))).andExpect(status().isForbidden());
        mvc.perform(as(adminEscuela, delete("/api/detalles-matricula/" + matricula))).andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    void validacionesYErrores() throws Exception {
        // H1.1: código modular MINEDU de 7 dígitos y único
        mvc.perform(as(admin, post("/api/colegios")).content("{\"codigoModular\":\"12AB\",\"nombre\":\"X\",\"departamento\":\"a\"," +
                "\"provincia\":\"b\",\"distrito\":\"c\",\"comunidad\":\"d\",\"tipo_zona\":\"e\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/colegios")).content("{\"codigoModular\":\"0501234\",\"nombre\":\"Duplicado\",\"departamento\":\"a\"," +
                "\"provincia\":\"b\",\"distrito\":\"c\",\"comunidad\":\"d\",\"tipo_zona\":\"e\"}")).andExpect(status().isConflict());
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"B\",\"seccion\":\"B\",\"capacidad\":-3,\"idColegio\":" + colegio + "}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"B\",\"seccion\":\"B\",\"capacidad\":10,\"computadoras\":-1,\"idColegio\":" + colegio + "}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(adminEscuela, post("/api/cursos")).content("{\"descripcion\":\"sin nombre\"}")).andExpect(status().isBadRequest());
        mvc.perform(as(adminEscuela, post("/api/periodos")).content("{\"nombre\":\"mal\",\"fechaInicio\":\"2026-08-01\",\"fechaFin\":\"2026-03-01\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(as(admin, post("/api/aula")).content("{\"nombre\":\"C\",\"seccion\":\"C\",\"capacidad\":10,\"idColegio\":99999}"))
                .andExpect(status().isNotFound());
        mvc.perform(as(admin, post("/api/grados")).content("{\"nombre\":\"1° Secundaria EBR\",\"nivel\":\"Secundaria\"}"))
                .andExpect(status().isConflict());
        mvc.perform(as(admin, delete("/api/colegios/" + colegio)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El registro está duplicado o tiene datos relacionados"));
    }
}
