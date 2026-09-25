package org.example.andina2026.serviceimplements;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Users;
import org.example.andina2026.repositories.IUsersRepository;


import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final IUsersRepository usersRepository;

    public JwtUserDetailsService(IUsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // H2.1: el identificador de inicio de sesión es el DNI
        Users user = usersRepository.findByDni(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username
                        )
                );

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRol()))
                .map(authority -> (GrantedAuthority) authority)
                .toList();

        return User.builder()
                .username(user.getDni())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(user.getEnabled()))
                .build();
    }
}