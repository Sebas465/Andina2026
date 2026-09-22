package org.example.andina2026.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @SecurityRequirements // público: Swagger no le envía el token (evita 401 si Authorize tiene algo inválido)
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request, HttpServletRequest http) {

        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );
        } catch (AuthenticationException ex) {
            // Log de seguridad (H2.1): usuario, IP y motivo; la contraseña nunca se registra
            securityLog.warn("LOGIN FALLIDO usuario='{}' ip={} motivo={}", request.getUsername(), http.getRemoteAddr(),
                    ex.getClass().getSimpleName());
            throw ex;
        }
        securityLog.info("LOGIN OK usuario='{}' ip={}", request.getUsername(), http.getRemoteAddr());

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
