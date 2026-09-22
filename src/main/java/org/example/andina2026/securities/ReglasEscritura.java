package org.example.andina2026.securities;

import java.util.LinkedHashMap;
import java.util.Map;

/** Generado por generar_andina.py: roles que pueden crear/modificar/eliminar en cada ruta (mismo criterio que @PreAuthorize). */
public final class ReglasEscritura {
    public static final Map<String, String[]> ROLES;

    static {
        Map<String, String[]> m = new LinkedHashMap<>();
        m.put("/api/colegios", new String[]{"ADMIN"});
        m.put("/api/aula", new String[]{"ADMIN"});
        m.put("/api/grados", new String[]{"ADMIN"});
        m.put("/api/roles-persona", new String[]{"ADMIN"});
        m.put("/api/personas", new String[]{"ADMIN", "ADMIN_ESCUELA", "LOCAL"});
        m.put("/api/periodos", new String[]{"ADMIN", "ADMIN_ESCUELA"});
        m.put("/api/matriculas", new String[]{"ADMIN", "ADMIN_ESCUELA", "LOCAL"});
        m.put("/api/cursos", new String[]{"ADMIN", "ADMIN_ESCUELA"});
        m.put("/api/asignaciones-docentes", new String[]{"ADMIN", "ADMIN_ESCUELA"});
        m.put("/api/materiales", new String[]{"ADMIN", "ADMIN_ESCUELA", "ESPECIALISTA", "LOCAL"});
        m.put("/api/detalles-matricula", new String[]{"ADMIN", "ADMIN_ESCUELA", "LOCAL"});
        m.put("/api/perfiles-academicos", new String[]{"ADMIN", "ADMIN_ESCUELA", "LOCAL"});
        m.put("/api/materiales-cursos", new String[]{"ADMIN", "ADMIN_ESCUELA", "ESPECIALISTA", "LOCAL"});
        m.put("/api/usuarios", new String[]{"ADMIN"});
        ROLES = Map.copyOf(m);
    }

    private ReglasEscritura() {
    }
}
