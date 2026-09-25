-- Migración: la cuenta de acceso pasa de users/roles a personas (Persona = usuario, Tipo_Persona = rol).
-- Para una BD que ya tiene las tablas users y roles. Uso:
--   psql -h localhost -U postgres -d Andina2026 -f migracion_persona_cuenta.sql
-- Cada cuenta de users pasa a ser una persona nueva con su DNI, su hash BCrypt y como tipo su rol
-- (ROLE_LOCAL → tipo LOCAL). Si su DNI ya existía en personas, la cuenta se le asigna a esa persona.
BEGIN;

ALTER TABLE personas ADD COLUMN IF NOT EXISTS dni VARCHAR(8);
ALTER TABLE personas ADD COLUMN IF NOT EXISTS password VARCHAR(200);
ALTER TABLE personas ADD COLUMN IF NOT EXISTS enabled BOOLEAN;
UPDATE personas SET enabled = TRUE WHERE enabled IS NULL;
ALTER TABLE personas ALTER COLUMN enabled SET NOT NULL;
ALTER TABLE personas ALTER COLUMN id_aula DROP NOT NULL;   -- el ADMIN y los especialistas no tienen aula
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_personas_dni') THEN
    ALTER TABLE personas ADD CONSTRAINT uk_personas_dni UNIQUE (dni);
  END IF;
END $$;

-- tipos de persona que dan acceso (Word H2.1)
INSERT INTO roles_persona (detalle)
SELECT t FROM (VALUES ('ADMIN'), ('ADMIN_ESCUELA'), ('ESPECIALISTA'), ('LOCAL')) v(t)
WHERE NOT EXISTS (SELECT 1 FROM roles_persona r WHERE UPPER(r.detalle) = v.t);

-- un rol por cuenta (si tenía varios, el de más permisos)
CREATE TEMP TABLE cuenta ON COMMIT DROP AS
SELECT DISTINCT ON (u.id) u.id, u.dni, u.username, u.password, u.enabled,
       REPLACE(r.rol, 'ROLE_', '') AS tipo
FROM users u JOIN roles r ON r.user_id = u.id
WHERE u.dni IS NOT NULL
ORDER BY u.id, CASE REPLACE(r.rol, 'ROLE_', '') WHEN 'ADMIN' THEN 1 WHEN 'ADMIN_ESCUELA' THEN 2
                                               WHEN 'ESPECIALISTA' THEN 3 ELSE 4 END;

-- DNI que ya es de una persona: se le agrega la cuenta
UPDATE personas p SET password = c.password, enabled = c.enabled,
       id_tipo_persona = (SELECT id_tipo_persona FROM roles_persona WHERE UPPER(detalle) = c.tipo LIMIT 1)
FROM cuenta c WHERE p.dni = c.dni;

-- el resto: persona nueva
INSERT INTO personas (codigo_estudiante, nombres, apellidos, estado, dni, password, enabled, id_tipo_persona)
SELECT 'EST-' || UPPER(SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 8)), c.username, 'Cuenta migrada', 'ACTIVO',
       c.dni, c.password, c.enabled,
       (SELECT id_tipo_persona FROM roles_persona WHERE UPPER(detalle) = c.tipo LIMIT 1)
FROM cuenta c WHERE NOT EXISTS (SELECT 1 FROM personas p WHERE p.dni = c.dni);

DROP TABLE roles;
DROP TABLE users;

COMMIT;
