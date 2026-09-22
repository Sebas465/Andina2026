package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.entities.Users;

import java.util.List;
import java.util.Optional;

public interface UsersServiceInterface {
    public List<Users> list();
    public Users insert(String username, String rawPassword, List<String> roles, Boolean enabled);
    public Optional<Users> listId(Long id);
    public boolean existsUsername(String username);
    public void delete(Long id);
}
