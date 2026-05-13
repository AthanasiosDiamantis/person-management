package com.saki.personmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - executed once per request.
 * JWT Authentifizierungs-Filter - wird einmal pro Request ausgeführt.
 *
 * Extracts JWT token from Authorization header, validates it,
 * and sets Authentication in SecurityContext.
 *
 * Extrahiert JWT Token aus dem Authorization Header, validiert ihn
 * und setzt die Authentication in den SecurityContext.
 *
 * @author saki
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extract JWT from request / JWT aus Request extrahieren
            String jwt = getJwtFromRequest(request);

            // TODO: remove after test
            log.info("JWT from request: {}", jwt != null ? "present" : "missing"); // ← NEU

            // 2. Validate token / Token validieren
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // 3. Extract username from token / Username aus Token extrahieren
                String username = tokenProvider.getUsernameFromToken(jwt);

                // 4. Load user from database / User aus Datenbank laden
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Create authentication object / Authentication-Objekt erstellen
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. Set authentication in SecurityContext
                // Authentication in SecurityContext setzen
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {}", username); // ← NEU als INFO
                log.debug("Authentication set for user: {} / Authentifizierung gesetzt für: {}",
                        username, username);
            }
        } catch (Exception ex) {
            log.error("Could not set authentication / Authentifizierung konnte nicht gesetzt werden: {}",
                    ex.getMessage());
        }

        // 7. Continue filter chain / Filter-Chain fortsetzen
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Authorization header.
     * Extrahiert JWT Token aus dem Authorization Header.
     *
     * Expected format: "Bearer <token>"
     * Erwartetes Format: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        // Prüfen ob Header vorhanden und mit "Bearer " beginnt
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix / "Bearer " entfernen
        }

        return null;
    }
}