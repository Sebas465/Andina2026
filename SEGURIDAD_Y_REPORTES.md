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
   En IntelliJ: Run → Edit Configurations → Environment variables.
3. `POST /login` con `{"username":"admin","password":"…"}` → `token`. En Swagger (`/swagger-ui/index.html`)
   pulsa **Authorize** y pega el token.
4. Crear cuentas: `POST /api/usuarios` (solo ADMIN) con `{"username","password","roles":["DOCENTE"]}`.
   Roles: `ADMIN`, `DOCENTE`, `ALUMNO`, `PSICOLOGO`.

## Tablas del ERD (13) → endpoints

| Tabla | Endpoint | Leer | Escribir |
|---|---|---|---|
| COLEGIO | `/api/colegios` | autenticado | ADMIN |
| AULA | `/api/aula` | autenticado | ADMIN |
| GRADO | `/api/grados` | autenticado | ADMIN |
| Rol (tipo de persona) | `/api/roles-persona` | autenticado | ADMIN |
| Persona | `/api/personas` | ADMIN, DOCENTE, PSICOLOGO (detalle `/{id}`: ADMIN) | ADMIN |
| PERIODO_ACADEMICO | `/api/periodos` | autenticado | ADMIN |
| MATRICULA | `/api/matriculas` | ADMIN, DOCENTE | ADMIN |
| Detalle Matricula | `/api/detalles-matricula` | ADMIN, DOCENTE | ADMIN |
| CURSO | `/api/cursos` | autenticado | ADMIN |
| ASIGNACION_DOCENTE | `/api/asignaciones-docentes` | autenticado | ADMIN |
| MATERIAL | `/api/materiales` | autenticado | ADMIN, DOCENTE |
| MATERIAL_CURSO | `/api/materiales-cursos` (borrar: `/{idMaterial}/{idCurso}`) | autenticado | ADMIN, DOCENTE |
| Perfil_Academico | `/api/perfiles-academicos` | ADMIN, PSICOLOGO, DOCENTE (detalle: ADMIN, PSICOLOGO) | ADMIN, PSICOLOGO |

Cada uno tiene `GET` lista, `GET /{id}`, `POST`, `PUT /{id}` y `DELETE /{id}`.

## Datos sensibles (nunca en listas)

- **Contraseñas**: solo se guardan como hash BCrypt; ninguna respuesta las devuelve (`UsersDTOList`).
- **Persona**: `correo` y `fechaNacimiento` no salen en la lista; solo en el detalle, para ADMIN.
- **Perfil académico**: `notas` y `estadoPsicologico` no salen en la lista; el detalle solo para ADMIN/PSICOLOGO.
- Errores de BD (duplicados, borrar con datos relacionados) → 409 con mensaje genérico, sin detalles internos.

## Reportes para decidir (`/api/reportes`, consultas nativas en `IReporteRepository`)

| Endpoint | Pregunta que responde | Roles |
|---|---|---|
| `alumnos-menor-promedio?limite=10` | ¿A quién apoyar primero? | ADMIN, DOCENTE, PSICOLOGO |
| `alumnos-en-riesgo` | ¿A quién atiende primero psicología? (desaprobado + observación; sin texto clínico) | ADMIN, PSICOLOGO |
| `rendimiento-colegios` | ¿Qué colegio necesita más recursos? (promedio, % desaprobados, zona) | ADMIN, DOCENTE |
| `ocupacion-aulas` | ¿Abrir secciones o redistribuir? | ADMIN |
| `carga-docente` | ¿Hay docentes sobrecargados? | ADMIN |
| `cursos-sin-docente/{idPeriodo}` | ¿Qué falta asignar en el periodo? | ADMIN |
| `cursos-sin-material` | ¿Dónde crear material primero? | ADMIN, DOCENTE |
| `matriculas-colegio-periodo` | ¿Cuánta demanda tiene cada colegio? | ADMIN |

## Decisiones sobre el ERD

- Columnas sin tipo en el ERD: `ID_Tipo_Persona`, `Id_D_Matricula`, `Id_Perfil_Academico` → BIGINT;
  `Detalle` → VARCHAR(100); `Detalles` y `Estado Psicologico` → TEXT; **`Notas` → DECIMAL(4,2)** (promedio 0–20,
  necesario para los rankings; aprobado desde 11).
- `ASIGNACION_DOCENTE.id_aula` no tenía relación dibujada: se enlazó con AULA.
- La tabla «Rol» del ERD (tipo de persona) es la entidad `Rol` en `roles_persona`; las cuentas de acceso usan
  `Users` / `Role` (tablas `users` / `roles`), igual que la demo.
- Correcciones al código existente: `@NotBlank` en campos numéricos (daba error 500), el aula no guardaba su
  colegio, longitudes de columna según el ERD y faltaba `spring-boot-starter-validation` (las validaciones no se
  aplicaban).

## Pruebas

`./mvnw test` (o `sh mvnw test`): 11 pruebas con H2 en modo PostgreSQL (no requiere PostgreSQL):
permisos por rol, ausencia de datos sensibles en listas, validaciones, CRUD de las 13 tablas y el resultado de
cada reporte.
