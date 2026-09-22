# Andina2026 — modelo completo, seguridad y reportes

Rama `step`. Sigue el patrón de `demoSM2_seguridad` (entities, IXRepository, XServiceInterface /
XServiceImplement, XDTOInsert / XDTOList, controllers con ModelMapper, securities/ con JWT).

## Arrancar

1. PostgreSQL con la base `Andina2026` (ver `application.properties`). Las tablas se crean solas (`ddl-auto=update`).
2. Primer arranque: define la contraseña del ADMIN inicial (solo se usa si la tabla `users` está vacía):
   ```bash
   export ANDINA_ADMIN_PASSWORD='UnaClaveSegura2026'   # usuario: admin (o ANDINA_ADMIN_USER)
   export JWT_SECRET='…64+ caracteres…'                  # opcional en desarrollo, obligatorio en producción
   ```
   El ADMIN inicial entra con el DNI `00000001` (o `ANDINA_ADMIN_DNI`).
   En IntelliJ: Run → Edit Configurations → Environment variables.
3. `POST /login` con `{"dni":"00000001","password":"…"}` → `token` (H2.1: se entra con **DNI**, sesión de 8 horas).
   En Swagger (`/swagger-ui/index.html`) pulsa **Authorize** y pega el token.
4. Crear cuentas: `POST /api/usuarios` (solo ADMIN) con `{"dni","username","password","roles":["LOCAL"]}`.
   Contraseña: 8+ caracteres con mayúscula, número y símbolo. Roles del Word: `ESPECIALISTA`, `LOCAL`,
   `ADMIN_ESCUELA`, más `ADMIN` (administrador del sistema, H1.1/H1.2).
5. Si tu BD tenía los datos de prueba anteriores, aplica `migracion_word.sql` (idempotente).

## Tablas del ERD (13) → endpoints

| Tabla | Endpoint | Leer | Escribir |
|---|---|---|---|
| COLEGIO (código modular MINEDU, historial) | `/api/colegios` | autenticado | ADMIN |
| AULA (grados atendidos, equipamiento, capacidad ≤ 40) | `/api/aula` | autenticado | ADMIN |
| GRADO | `/api/grados` | autenticado | ADMIN |
| Rol (tipo de persona) | `/api/roles-persona` | autenticado | ADMIN |
| Persona (lengua materna, edad 12-16, ID anonimizado) | `/api/personas` | personal docente (detalle `/{id}`: ADMIN, ADMIN_ESCUELA, LOCAL) | ADMIN, ADMIN_ESCUELA, LOCAL |
| PERIODO_ACADEMICO | `/api/periodos` | autenticado | ADMIN, ADMIN_ESCUELA |
| MATRICULA | `/api/matriculas` | personal docente | ADMIN, ADMIN_ESCUELA, LOCAL |
| Detalle Matricula | `/api/detalles-matricula` | personal docente | ADMIN, ADMIN_ESCUELA, LOCAL |
| CURSO | `/api/cursos` | autenticado | ADMIN, ADMIN_ESCUELA |
| ASIGNACION_DOCENTE | `/api/asignaciones-docentes` | autenticado | ADMIN, ADMIN_ESCUELA |
| MATERIAL | `/api/materiales` | autenticado | personal docente |
| MATERIAL_CURSO | `/api/materiales-cursos` (borrar: `/{idMaterial}/{idCurso}`) | autenticado | personal docente |
| Perfil_Academico | `/api/perfiles-academicos` | personal docente (detalle: ADMIN, ADMIN_ESCUELA, LOCAL) | ADMIN, ADMIN_ESCUELA, LOCAL |
| Historial de cambios | `/api/auditoria?entidad=Persona&idRegistro=…` | ADMIN, ADMIN_ESCUELA, LOCAL | (automático) |

«Personal docente» = ADMIN, ADMIN_ESCUELA, ESPECIALISTA y LOCAL. Los permisos de escritura se comprueban antes de leer
la petición (`securities/ReglasEscritura`, generado con el mismo criterio que `@PreAuthorize`): sin permiso → 403.

Cada uno tiene `GET` lista, `GET /{id}`, `POST`, `PUT /{id}` y `DELETE /{id}`.

## Datos sensibles (nunca en listas)

- **Contraseñas**: solo se guardan como hash BCrypt; ninguna respuesta las devuelve (`UsersDTOList`).
- **Persona**: `correo` y `fechaNacimiento` no salen en la lista; solo en el detalle, para ADMIN, ADMIN_ESCUELA y LOCAL.
- **Perfil académico**: `notas` y `estadoPsicologico` no salen en la lista; el detalle solo para ADMIN, ADMIN_ESCUELA y LOCAL.
- Errores de BD (duplicados, borrar con datos relacionados) → 409 con mensaje genérico, sin detalles internos.

## Reportes para decidir (`/api/reportes`, consultas nativas en `IReporteRepository`)

| Endpoint | Pregunta que responde | Roles |
|---|---|---|
| `alumnos-menor-promedio?limite=10&lengua=QUECHUA&idGrado=…` | ¿A quién apoyar primero? (filtros H6.1) | personal docente |
| `alumnos-menor-promedio/csv` | La misma lista para Excel (H6.2) | personal docente |
| `alumnos-en-riesgo` | Desaprobado + observación psicológica (sin texto clínico) | ADMIN, ADMIN_ESCUELA, LOCAL |
| `rendimiento-colegios` | ¿Qué colegio necesita más recursos? | personal docente |
| `escuelas-inactivas?dias=30` | Escuelas sin cambios ni matrículas en N días (H1.1) | ADMIN, ADMIN_ESCUELA |
| `ocupacion-aulas` | ¿Abrir secciones o redistribuir? | ADMIN, ADMIN_ESCUELA |
| `carga-docente` | ¿Hay docentes sobrecargados? | ADMIN, ADMIN_ESCUELA |
| `cursos-sin-docente/{idPeriodo}` | ¿Qué falta asignar en el periodo? | ADMIN, ADMIN_ESCUELA |
| `cursos-sin-material` | ¿Dónde crear material primero? | personal docente |
| `matriculas-colegio-periodo` | ¿Cuánta demanda tiene cada colegio? | ADMIN, ADMIN_ESCUELA |

## Cambios para cumplir el Word (Trabajo Parcial)

**Cambios al ERD (modelo de negocio)** — solo estos van al diagrama:
`colegios.codigo_modular`, `aulas.computadoras` / `proyectores` / `conexion_mbps`, la tabla intermedia `aula_grado`
(aula ↔ grados atendidos), `personas.lengua_materna` y `personas.codigo_estudiante`.

**Tablas de la capa de seguridad y técnicas (NO van en el ERD):** `users` (con `dni`), `roles` y `auditoria`.
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
  modelo. Los roles de acceso (ESPECIALISTA, LOCAL, ADMIN_ESCUELA, ADMIN) son de seguridad: viven en `Users` / `Role`
  (tablas `users` / `roles`), fuera del ERD, igual que en la demo.
- Correcciones al código existente: `@NotBlank` en campos numéricos (daba error 500), el aula no guardaba su
  colegio, longitudes de columna según el ERD y faltaba `spring-boot-starter-validation` (las validaciones no se
  aplicaban).

## Pruebas

`./mvnw test` (o `sh mvnw test`): 13 pruebas con H2 en modo PostgreSQL (no requiere PostgreSQL):
login con DNI, roles del Word, historial, matrícula (edad, lengua, cupo), permisos por rol, ausencia de datos sensibles
en listas, validaciones, CRUD de las 13 tablas y el resultado de cada reporte. Cobertura (JaCoCo): `target/site/jacoco`.
