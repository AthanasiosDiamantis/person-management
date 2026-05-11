package com.saki.personmanagement.repository;

import com.saki.personmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing user in database.
 *
 * @author saki
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username
     * SQL: SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a username already exists in the database.
     * SQL: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     * @param username 1 or more characters
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
