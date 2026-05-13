package com.saki.personmanagement.controller;

import com.saki.personmanagement.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for JWT authentication.
 * REST-Controller für JWT Authentifizierung.
 *
 * @author saki
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Login endpoint - returns JWT token on success.
     * Login-Endpunkt - gibt JWT Token bei Erfolg zurück.
     *
     * @param loginRequest username and password / Username und Passwort
     * @return JWT token response / JWT Token Antwort
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        // 1. Authenticate user / User authentifizieren
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT token / JWT Token generieren
        String jwt = tokenProvider.generateToken(authentication);

        log.info("Login successful for user: {}", loginRequest.getUsername());

        // 4. Return token / Token zurückgeben
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    // ==================== REQUEST / RESPONSE CLASSES ====================

    /**
     * Login request body / Login-Anfrage Body
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * JWT response body / JWT Antwort Body
     */
    @Data
    @RequiredArgsConstructor
    public static class JwtResponse {
        private final String token;
        private final String type = "Bearer";
    }
}