package org.example.andina2026.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.andina2026.dtos.LoginRequestDTO;
import org.example.andina2026.dtos.LoginResponseDTO;
import org.example.andina2026.securities.JwtTokenService;

@RestController
@RequestMapping("/login")
public class LoginController {
    // H2.1: registro de intentos de inicio de sesión (la contraseña nunca se registra)
    private static final Logger securityLog = LoggerFactory.getLogger("andina.security.login");

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    public LoginController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService) {

        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getDni(), // H2.1: se inicia sesión con el DNI
                                    request.getPassword()
                            )
                    );
        } catch (AuthenticationException ex) {
            securityLog.warn("LOGIN FALLIDO dni='{}'", request.getDni());
            throw ex;
        }
        securityLog.info("LOGIN OK dni='{}'", request.getDni());

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token =
                jwtTokenService.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        userDetails.getUsername()
                )
        );
    }
}
