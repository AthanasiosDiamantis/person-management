package com.saki.personmanagement.security;

import com.saki.personmanagement.model.User;
import com.saki.personmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of UserDetailsService.
 *
 * @author saki
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user's data from the database. called automatically by Spring Security on login.
     * @param username the username to search for
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        log.info("User found: {} with role: {}", user.getUsername(), user.getRole());

        // attention User is not from own model, but from spring security, therefore
        // the full qualified name is required
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.isAccountNonLocked(),
                getAuthorities(user)
        );
    }

    /**
     * Converts our Role enum to Spring Security's GrantedAuthority.
     * Konvertiert unser Role-Enum in Spring Security's GrantedAuthority.
     *
     * Spring Security expects roles with prefix "ROLE_"
     * Spring Security erwartet Rollen mit Prefix "ROLE_"
     * Example: Role.ADMIN → "ROLE_ADMIN"
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
