package org.example.andina2026.serviceimplements;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.andina2026.entities.Role;
import org.example.andina2026.entities.Users;
import org.example.andina2026.repositories.IUsersRepository;
import org.example.andina2026.serviceinterfaces.UsersServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImplement implements UsersServiceInterface {
    private final IUsersRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImplement(IUsersRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> list() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Users insert(String username, String rawPassword, List<String> roles, Boolean enabled) {
        Users u = new Users();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword)); // solo se guarda el hash BCrypt
        u.setEnabled(enabled == null || enabled);
        for (String rol : roles.stream().distinct().toList()) {
            Role r = new Role();
            r.setRol("ROLE_" + rol);
            r.setUser(u);
            u.getRoles().add(r);
        }
        return repository.save(u);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Users> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
