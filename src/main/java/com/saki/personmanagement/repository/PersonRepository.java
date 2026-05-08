package com.saki.personmanagement.repository;

import com.saki.personmanagement.model.AddressType;
import com.saki.personmanagement.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    // ==================== BASIC QUERY METHODS ====================

    /**
     * Finds a person by email address (exact match).
     * SQL: SELECT * FROM persons WHERE email = ?
     * <p>Spring Data JPA generates the SQL automatically from the method name!</p>
     *
     * @param email the email address to search for
     * @return the person wrapped in Optional, or empty if not found
     */
    Optional<Person> findByEmail(String email);

    /**
     * Finds persons by last name (exact match).
     * SQL: SELECT * FROM persons WHERE last_name = ?
     */
    List<Person> findByLastName(String lastName);

    /**
     * Finds persons by first name (case-insensitive, partial match).
     * SQL: SELECT * FROM persons WHERE LOWER(first_name) LIKE LOWER('%value%')
     */
    List<Person> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Finds persons by first and last name.
     * SQL: SELECT * FROM persons WHERE first_name = ? AND last_name = ?
     */
    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Finds persons by last name, sorted by first name ascending.
     * SQL: SELECT * FROM persons WHERE last_name = ? ORDER BY first_name ASC
     */
    List<Person> findByLastNameOrderByFirstNameAsc(String lastName);

    // ==================== RELATIONSHIP QUERIES ====================

    /**
     * Finds persons with an address in the given city.
     * SQL: SELECT DISTINCT p.* FROM persons p JOIN addresses a ON p.id = a.person_id WHERE a.city = ?
     */
    List<Person> findByAddresses_City(String city);

    /**
     * Finds persons with a specific address type.
     * SQL: SELECT DISTINCT p.* FROM persons p JOIN addresses a ON p.id = a.person_id WHERE a.address_type = ?
     */
    List<Person> findByAddresses_AddressType(AddressType addressType);

    // ==================== CUSTOM JPQL QUERIES ====================

    /**
     * Loads all persons with their addresses in a single query (solves N+1 problem).
     * <p>
     * JOIN FETCH loads addresses in the same query - no additional SQL per person!
     */
    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.addresses")
    List<Person> findAllWithAddresses();

    /**
     * Loads a single person with all addresses by ID.
     */
    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.addresses WHERE p.id = :id")
    Optional<Person> findByIdWithAddresses(@Param("id") Long id);

}
