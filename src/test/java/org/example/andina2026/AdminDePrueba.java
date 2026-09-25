package org.example.andina2026;

import org.example.andina2026.entities.Persona;
import org.example.andina2026.entities.Rol;
import org.example.andina2026.repositories.IPersonaRepository;
import org.example.andina2026.repositories.IRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Solo para las pruebas (H2 vacía): crea el tipo de persona ADMIN y la persona ADMIN con la que inician sesión.
 * En PostgreSQL las cuentas se cargan con el script de datos, igual que en demoSM2_seguridad.
 */
@Component
class AdminDePrueba implements CommandLineRunner {
    private final IPersonaRepository personaRepository;
    private final IRolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    AdminDePrueba(IPersonaRepository personaRepository, IRolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (personaRepository.findByDni("00000001").isPresent()) {
            return;
        }
        Rol tipoAdmin = new Rol();
        tipoAdmin.setDetalle("ADMIN");
        rolRepository.save(tipoAdmin);

        Persona admin = new Persona();
        admin.setNombres("Administrador");
        admin.setApellidos("del sistema");
        admin.setDni("00000001");
        admin.setPassword(passwordEncoder.encode("AdminPrueba2026"));
        admin.setEnabled(true);
        admin.setRol(tipoAdmin);
        personaRepository.save(admin);
    }
}
