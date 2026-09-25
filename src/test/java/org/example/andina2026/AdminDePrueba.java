package org.example.andina2026;

import org.example.andina2026.entities.Role;
import org.example.andina2026.entities.Users;
import org.example.andina2026.repositories.IUsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Solo para las pruebas (H2 vacía): crea el ADMIN con el que inician sesión.
 * En PostgreSQL los usuarios se cargan con el script de datos, igual que en demoSM2_seguridad.
 */
@Component
class AdminDePrueba implements CommandLineRunner {
    private final IUsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    AdminDePrueba(IUsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usersRepository.findByDni("00000001").isPresent()) {
            return;
        }
        Users admin = new Users();
        admin.setDni("00000001");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("AdminPrueba2026"));
        admin.setEnabled(true);
        Role rol = new Role();
        rol.setRol("ROLE_ADMIN");
        rol.setUser(admin);
        admin.getRoles().add(rol);
        usersRepository.save(admin);
    }
}
