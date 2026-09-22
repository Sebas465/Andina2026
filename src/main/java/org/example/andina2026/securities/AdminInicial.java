package org.example.andina2026.securities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.example.andina2026.repositories.IUsersRepository;
import org.example.andina2026.serviceinterfaces.UsersServiceInterface;

import java.util.List;

/**
 * Crea el primer ADMIN solo si la tabla users está vacía y se definió ANDINA_ADMIN_PASSWORD.
 * Así ninguna contraseña queda escrita en el código ni en GitHub.
 */
@Component
public class AdminInicial implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);
    private final IUsersRepository repository;
    private final UsersServiceInterface usersService;
    private final String dni;
    private final String username;
    private final String password;

    public AdminInicial(IUsersRepository repository, UsersServiceInterface usersService,
                        @Value("${andina.admin.dni:00000001}") String dni,
                        @Value("${andina.admin.username:admin}") String username,
                        @Value("${andina.admin.password:}") String password) {
        this.repository = repository;
        this.usersService = usersService;
        this.dni = dni;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        if (password == null || password.length() < 8) {
            log.warn("No hay usuarios. Define ANDINA_ADMIN_PASSWORD (mínimo 8 caracteres) para crear el ADMIN inicial.");
            return;
        }
        usersService.insert(dni, username, password, List.of("ADMIN"), true);
        log.info("Usuario ADMIN inicial '{}' (DNI {}) creado.", username, dni);
    }
}
