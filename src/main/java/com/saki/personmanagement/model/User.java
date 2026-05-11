package com.saki.personmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a system for user authentication.
 *
 * <p>Passwords are ALWAYS stored encrypted - never in plain text</p>
 *
 * @author saki
 */
@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Password - ALWAYS storted as BCrypt hash!!!
     */
    @NotBlank(message = "Password must not be blank")
    @Column(nullable = false)
    private String password;

    /**
     * User role - determines what the user can do.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    /**
     * Whether the user is enabled or not.
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Whether the user's account is locked or not.
     */
    @Column(nullable = false)
    private boolean accountNonLocked = true;
}
