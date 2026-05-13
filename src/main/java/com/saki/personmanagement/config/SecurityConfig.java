package com.saki.personmanagement.config;

import com.saki.personmanagement.security.CustomUserDetailsService;
import com.saki.personmanagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration.
 * Zentrale Spring Security Konfiguration.
 *
 * @author saki
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * BCrypt password encoder.
     * BCrypt Passwort-Encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider - connects UserDetailsService with PasswordEncoder.
     * Authentication Provider - verbindet UserDetailsService mit PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Authentication manager.
     * Authentication Manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Security filter chain - defines security rules.
     * Security Filter Chain - definiert Sicherheitsregeln.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())

                // Disable CSRF for API endpoints / CSRF für API-Endpunkte deaktivieren
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints / Öffentliche Endpunkte
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()

                        // JWT login endpoint - public / JWT Login-Endpunkt - öffentlich
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin-only endpoints / Nur für Admins
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // API endpoints - authenticated / API-Endpunkte - authentifiziert
                        .requestMatchers("/api/**").authenticated()

                        // All other endpoints / Alle anderen Endpunkte
                        .anyRequest().authenticated()
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                // JWT Filter vor UsernamePasswordAuthenticationFilter einfügen
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // Session management: STATELESS for API / Stateless für API
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("sakiPersonManagementKey")
                        .tokenValiditySeconds(60 * 60 * 24 * 30)
                        .rememberMeParameter("remember-me")
                )
                .httpBasic(basic -> {});

        return http.build();
    }
}