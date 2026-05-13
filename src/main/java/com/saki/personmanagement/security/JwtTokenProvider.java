package com.saki.personmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Utility class for JWT token generation, validation and parsing.
 * Hilfsklasse für JWT Token-Erstellung, Validierung und Parsing.
 *
 * @author saki
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * Constructor - creates SecretKey from configured secret string.
     * Konstruktor - erstellt SecretKey aus konfiguriertem Secret-String.
     */
    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the authenticated user.
     * Erstellt einen JWT Token für den authentifizierten User.
     *
     * @param authentication the authenticated user / der authentifizierte User
     * @return signed JWT token string / signierter JWT Token String
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Collect all authorities as comma-separated string
        // Alle Authorities als kommaseparierter String sammeln
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username)                    // Username im Token speichern
                .claim("authorities", authorities)    // Authorities im Token speichern
                .issuedAt(now)                        // Ausstellungszeitpunkt
                .expiration(expiryDate)               // Ablaufzeitpunkt
                .signWith(secretKey)                  // Token signieren
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     * Extrahiert den Username aus einem JWT Token.
     *
     * @param token the JWT token / der JWT Token
     * @return the username / der Username
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates a JWT token.
     * Validiert einen JWT Token.
     *
     * @param token the JWT token to validate / der zu validierende JWT Token
     * @return true if valid, false otherwise / true wenn gültig, sonst false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired / JWT Token abgelaufen: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token unsupported / JWT Token nicht unterstützt: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token malformed / JWT Token fehlerhaft: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token empty / JWT Token leer: {}", e.getMessage());
        }
        return false;
    }
}