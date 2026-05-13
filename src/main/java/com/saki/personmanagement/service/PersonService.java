package com.saki.personmanagement.service;

import com.saki.personmanagement.config.CacheConfig;
import com.saki.personmanagement.model.*;
import com.saki.personmanagement.repository.OrderRepository;
import com.saki.personmanagement.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for person management business logic.
 *
 * @author saki
 */
@Service
@Transactional
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    public PersonService(PersonRepository personRepository, OrderRepository orderRepository, EmailService emailService) {
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    // ==================== PERSON CRUD ====================

    /**
     * Returns all persons from the database.
     */
    @Cacheable(value = CacheConfig.CACHE_PERSONS)
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Returns all persons with addresses in a single query (solves N+1).
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> getAllPersonsWithAddresses() {
        return personRepository.findAllWithAddresses();
    }

    /**
     * Finds a person by ID.
     */
    @Cacheable(value = CacheConfig.CACHE_PERSONS, key = "#id")
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Creates a new person with addresses in the database.
     */
    @CacheEvict(value = CacheConfig.CACHE_PERSONS, allEntries = true) // delete all cached persons
    @PreAuthorize("hasRole('ADMIN')") /* only admins can create persons */
    public Person createPerson(Person person) {
        // Synchronize both sides of the relationship before saving.
        if (person.getAddresses() != null) {
            person.getAddresses().forEach(address -> address.setPerson(person));
        }
        Person saved = personRepository.save(person);

        // Send welcome email to the new person
        if(saved.getEmail() != null && !saved.getEmail().isEmpty()) {
            emailService.sendPersonCreatedEmailAsync(
                    saved.getEmail(),
                    saved.getFirstName(),
                    saved.getLastName(),
                    saved.getEmail()
            );
        }
        return saved;
    }

    /**
     * Updates an existing person.
     */
    @CacheEvict(value = CacheConfig.CACHE_PERSONS, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')") /* only admins can update persons */
    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        person.setFirstName(personDetails.getFirstName());
        person.setLastName(personDetails.getLastName());
        person.setEmail(personDetails.getEmail());

        return personRepository.save(person);
    }

    /**
     * Deletes a person by ID.
     */
    @CacheEvict(value = CacheConfig.CACHE_PERSONS, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')") /* only admins can delete persons */
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    // ==================== ADDRESS MANAGEMENT ====================

    /**
     * Adds an address to an existing person.
     */
    @PreAuthorize("hasRole('ADMIN')") /* only admins can add addresses */
    public Optional<Person> addAddress(Long personId, Address address) {
        return personRepository.findById(personId)
                .map(person -> {
                    person.addAddress(address); // Helper method synchronizes both sides!
                    return personRepository.save(person);
                });
    }

    // ==================== SEARCH METHODS ====================

    /**
     * Finds persons by last name.
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> findByLastName(String lastName) {
        return personRepository.findByLastName(lastName);
    }

    /**
     * Finds persons by first name (partial, case-insensitive).
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> findByFirstNameContaining(String firstName) {
        return personRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    /**
     * Finds a person by email.
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    /**
     * Finds persons with an address in the given city.
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> findByCity(String city) {
        return personRepository.findByAddresses_City(city);
    }

    /**
     * Finds persons by address type.
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Person> findByAddressType(AddressType addressType) {
        return personRepository.findByAddresses_AddressType(addressType);
    }

    // ==================== ORDER MANAGEMENT ====================

    /**
     * adds an order to an existing person.
     *
     * @param personId id of the person
     * @param order    order to be added
     * @return updated person
     */
    @PreAuthorize("hasRole('ADMIN')") /* only admins can add orders */
    public Optional<Person> addOrder(Long personId, Order order) {
        return personRepository.findById(personId)
                .map(person -> {
                    person.addOrder(order); // Helper method synchronizes both sides!
                    return personRepository.save(person);
                });
    }

    /**
     * Finds order by status
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Finds order by person ID
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Order> findOrdersByPersonId(Long personId) {
        return orderRepository.findByPersonId(personId);
    }

    /**
     * Finds the 10 most recent orders.
     */
    @PreAuthorize("isAuthenticated()") /* all authenticated users can access this endpoint */
    public List<Order> findLatestOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }
}