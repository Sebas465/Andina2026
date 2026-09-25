package org.example.andina2026.serviceimplements;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.andina2026.entities.Persona;
import org.example.andina2026.repositories.IPersonaRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final IPersonaRepository personaRepository;

    public JwtUserDetailsService(IPersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // H2.1: el identificador de inicio de sesión es el DNI de la persona
        Persona persona = personaRepository.findByDni(username)
                .filter(p -> p.getPassword() != null) // sin contraseña = sin cuenta
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username
                        )
                );

        // el Tipo_Persona es el rol: LOCAL → ROLE_LOCAL
        return User.builder()
                .username(persona.getDni())
                .password(persona.getPassword())
                .authorities(new SimpleGrantedAuthority(persona.getRol().getAuthority()))
                .disabled(!Boolean.TRUE.equals(persona.getEnabled()))
                .build();
    }
}
