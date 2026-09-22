package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Users;

import java.util.List;
import java.util.Optional;

public interface UsersServiceInterface {
    public List<Users> list();
    public Users insert(String dni, String username, String passwordHash, List<String> roles, Boolean enabled);
    public Optional<Users> listId(Long id);
    public boolean existsUsername(String username);
    public boolean existsDni(String dni);
    public void delete(Long id);
}
