# Andina2026 — modelo completo, seguridad y reportes

Rama `step`. Sigue el patrón de `demoSM2_seguridad` (entities, IXRepository, XServiceInterface /
XServiceImplement, XDTOInsert / XDTOList, controllers con ModelMapper, securities/ con JWT).

## Arrancar

1. PostgreSQL con la base `Andina2026` (ver `application.properties`). Las tablas se crean solas (`ddl-auto=update`).
2. Carga los usuarios y datos con el script `datos_andina2026.sql` (o `crear_bd_andina2026.sql` en una BD vacía),
   igual que en `demoSM2_seguridad`: los usuarios se insertan en la BD con su contraseña ya hasheada (BCrypt).
   El ADMIN entra con el DNI `76588404` (en una BD que ya tenía el admin 00000001:
   `UPDATE personas SET dni = '76588404' WHERE dni = '00000001';`).
3. `POST /login` con `{"dni":"76588404","password":"…"}` → `token` (H2.1: se entra con **DNI**, sesión de 8 horas).
   En Swagger (`/swagger-ui/index.html`) pulsa **Authorize** y pega el token.
4. Crear cuentas: `POST /api/personas` (solo ADMIN) con `dni`, `password` e `idRol` de un tipo con acceso
   (`ADMIN`, `ADMIN_ESCUELA`, `ESPECIALISTA` o `LOCAL`). La persona ES el usuario y su Tipo_Persona es su rol.
   Una BD que aún tenga `users`/`roles` se pasa al modelo nuevo con `migracion_diagrama.sql`.
   Contraseña: 8+ caracteres con mayúscula, número y símbolo. Roles del Word: `ESPECIALISTA`, `LOCAL`,
   `ADMIN_ESCUELA`, más `ADMIN` (administrador del sistema, H1.1/H1.2).
5. Si tu BD tenía los datos de prueba anteriores, aplica `migracion_word.sql` (idempotente).

## Tablas del ERD (13) → endpoints

| Tabla | Endpoint | Leer | Escribir |
|---|---|---|---|
| COLEGIO (código modular MINEDU, historial) | `/api/colegios` | autenticado | ADMIN |
| AULA (equipamiento, capacidad ≤ 40) | `/api/aula` | autenticado | ADMIN |
| GRADO | `/api/grados` | autenticado | ADMIN |
| Rol (tipo de persona) | `/api/roles-persona` | ADMIN | ADMIN |
| Persona (lengua materna, edad 12-16, ID anonimizado) | `/api/personas` | personal docente (también el detalle `/{id}`) | ADMIN, ADMIN_ESCUELA, LOCAL |
| PERIODO_ACADEMICO | `/api/periodos` | autenticado | ADMIN, ADMIN_ESCUELA |
| MATRICULA | `/api/matriculas` | personal docente | **solo ADMIN** |
| Detalle Matricula | `/api/detalles-matricula` | personal docente | **solo ADMIN** |
| CURSO | `/api/cursos` | autenticado | ADMIN, ADMIN_ESCUELA |
| ASIGNACION_DOCENTE | `/api/asignaciones-docentes` | autenticado | ADMIN, ADMIN_ESCUELA |
| MATERIAL | `/api/materiales` | autenticado | personal docente |
| MATERIAL_CURSO | `/api/materiales-cursos` (borrar: `/{idMaterial}/{idCurso}`) | autenticado | personal docente |
| Perfil_Academico | `/api/perfiles-academicos` | personal docente (también el detalle con notas) | ADMIN, ADMIN_ESCUELA, LOCAL |
| Historial de cambios | `/api/auditoria?entidad=Persona&idRegistro=…` | personal docente | (automático) |

«Personal docente» = ADMIN, ADMIN_ESCUELA, ESPECIALISTA y LOCAL. Las matrículas solo las crea, modifica o elimina el ADMIN. Los permisos se comprueban con `@PreAuthorize`
en cada método (sin permiso → 403), igual que en `demoSM2_seguridad`.

Seguridad igual que la demo (`securities/`: `SecurityConfig`, `JwtConfig`, `JwtTokenService`,
`CustomJwtAuthenticationConverter`, `OpenApiConfig`; `LoginController`; `JwtUserDetailsService`). Únicas diferencias,
porque las pide el Word (H2.1): se inicia sesión con **DNI**, el token dura **8 horas** (en la demo 5) y el login registra
cada intento (`LOGIN OK` / `LOGIN FALLIDO`).

Cada uno tiene `GET` lista, `GET /{id}`, `POST`, `PUT /{id}` y `DELETE /{id}`.

## Datos sensibles (nunca en listas)

- **Contraseñas**: se hashean con BCrypt en `UsersController.registrar` (`passwordEncoder.encode`, bean de `SecurityConfig`)
  y solo se guarda el hash; ninguna respuesta las devuelve (`UsersDTOList`).
- **Persona**: `correo` y `fechaNacimiento` no salen en la lista; solo en el detalle (personal docente, incluido ESPECIALISTA).
- **Perfil académico**: `notas` y `estadoPsicologico` no salen en la lista; solo en el detalle (personal docente, incluido ESPECIALISTA).
- Errores de BD (duplicados, borrar con datos relacionados) → 409 con mensaje genérico, sin detalles internos.

## Reportes para decidir (cada uno dentro del controller de su entidad)

La consulta nativa (`@Query(nativeQuery = true)`) vive en el repository de su entidad, el service la expone y el
controller arma la respuesta. Si la consulta devuelve filas de una entidad se pasan al DTO con `modelMapper.map(...)`;
si calcula totales, devuelve `List<Object[]>` y cada fila se pasa al DTO con setters.

| Endpoint | Pregunta que responde | Roles |
|---|---|---|
| `/api/perfiles-academicos/reporte-menor-promedio?limite=10&lengua=QUECHUA&idGrado=…` | ¿A quién apoyar primero? (filtros H6.1) | personal docente |
| `/api/perfiles-academicos/reporte-menor-promedio/csv` | La misma lista para Excel (H6.2) | personal docente |
| `/api/personas/reporte-en-riesgo` | Desaprobado + observación psicológica (devuelve la ficha del alumno) | personal docente |
| `/api/colegios/reporte-rendimiento` | ¿Qué colegio necesita más recursos? | personal docente |
| `/api/colegios/reporte-inactivos?dias=30` | Escuelas sin cambios ni matrículas en N días (H1.1) | ADMIN, ADMIN_ESCUELA |
| `/api/colegios/reporte-matriculas` | ¿Cuánta demanda tiene cada colegio por periodo? | ADMIN, ADMIN_ESCUELA |
| `/api/aula/reporte-ocupacion` | ¿Abrir secciones o redistribuir? Alumnos por aula | ADMIN, ADMIN_ESCUELA |
| `/api/asignaciones-docentes/reporte-carga-docente` | ¿Hay docentes sobrecargados? | ADMIN, ADMIN_ESCUELA |
| `/api/cursos/reporte-sin-docente/{idPeriodo}` | ¿Qué falta asignar en el periodo? | ADMIN, ADMIN_ESCUELA |
| `/api/cursos/reporte-sin-material` | ¿Dónde crear material primero? | personal docente |

`ReporteAgrupadoDTO` (categoría + cantidad) se reutiliza en ocupación de aulas, carga docente y matrículas por colegio.

## Cambios para cumplir el Word (Trabajo Parcial)

**Cambios al ERD (modelo de negocio)** — solo estos van al diagrama:
`colegios.codigo_modular`, `aulas.computadoras` / `proyectores` / `conexion_mbps`, `personas.lengua_materna` y `personas.codigo_estudiante`.

**Tabla técnica (NO va en el ERD):** `auditoria`. Las cuentas viven en `personas` (Persona = usuario, Tipo_Persona = rol).
El grado del alumno va en `detalles_matricula` por periodo (ya no existe `aula_grado`).
No son parte del modelo de negocio: las usa el código (Spring Security en `securities/` para el login con JWT, y el
registro de cambios) y JPA las crea solo al arrancar, igual que en `demoSM2_seguridad`.

Pendiente del Word
(no incluido en este avance): recuperación de contraseña por correo, envío automático de reportes por correo y los
épicos 3, 4 y 5 (videoconferencia, IA de ejercicios, sesiones), que necesitan tablas nuevas en el ERD.

## Decisiones sobre el ERD

- Columnas sin tipo en el ERD: `ID_Tipo_Persona`, `Id_D_Matricula`, `Id_Perfil_Academico` → BIGINT;
  `Detalle` → VARCHAR(100); `Detalles` y `Estado Psicologico` → TEXT; **`Notas` → DECIMAL(4,2)** (promedio 0–20,
  necesario para los rankings; aprobado desde 11).
- `ASIGNACION_DOCENTE.id_aula` no tenía relación dibujada: se enlazó con AULA.
- La tabla «Rol» del ERD (tipo de persona: alumno, docente…) es la entidad `Rol` en `roles_persona` y SÍ es del
  modelo. **Persona es también el usuario** (columnas `dni`, `password` con hash BCrypt y `enabled`) y **Tipo_Persona
  es el rol de seguridad**: el tipo `LOCAL` da `ROLE_LOCAL`, etc. Ya no existen las tablas `users` / `roles`.
  Solo el ADMIN pone contraseñas o asigna los tipos con acceso (ADMIN, ADMIN_ESCUELA, ESPECIALISTA, LOCAL).
  `personas.id_aula` es opcional (el ADMIN y los especialistas no tienen aula); para un alumno sigue siendo obligatorio.
- Correcciones al código existente: `@NotBlank` en campos numéricos (daba error 500), el aula no guardaba su
  colegio, longitudes de columna según el ERD y faltaba `spring-boot-starter-validation` (las validaciones no se
  aplicaban).

## Pruebas

`./mvnw test` (o `sh mvnw test`): 13 pruebas con H2 en modo PostgreSQL (no requiere PostgreSQL):
login con DNI, roles del Word, historial, matrícula (edad, lengua, cupo), permisos por rol, ausencia de datos sensibles
en listas, validaciones, CRUD de las 13 tablas y el resultado de cada reporte. Cobertura (JaCoCo): `target/site/jacoco`.
