package com.saki.personmanagement.repository;

import com.saki.personmanagement.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Person database access.
 *
 * <p>Spring Data JPA automatically generates the implementation at runtime.</p>
 *
 * <p>Provided automatically:</p>
 * <ul>
 *   <li>save(person) – saves or updates</li>
 *   <li>findById(id) – finds by ID</li>
 *   <li>findAll() – returns all</li>
 *   <li>deleteById(id) – deletes by ID</li>
 *   <li>count() – counts all</li>
 * </ul>
 *
 * @author saki
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a person by email address.
     *
     * <p>Spring Data JPA generates the SQL automatically from the method name!</p>
     *
     * @param email the email address to search for
     * @return the person wrapped in Optional, or empty if not found
     */
    Optional<Person> findByEmail(String email);


}
