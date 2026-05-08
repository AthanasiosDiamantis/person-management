package com.saki.personmanagement.service;

import com.saki.personmanagement.model.*;
import com.saki.personmanagement.repository.OrderRepository;
import com.saki.personmanagement.repository.PersonRepository;
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
public class PersonService {

    private final PersonRepository personRepository;
    private final OrderRepository orderRepository;

    public PersonService(PersonRepository personRepository, OrderRepository orderRepository) {
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
    }

    // ==================== PERSON CRUD ====================

    /**
     * Returns all persons from the database.
     */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Returns all persons with addresses in a single query (solves N+1).
     */
    public List<Person> getAllPersonsWithAddresses() {
        return personRepository.findAllWithAddresses();
    }

    /**
     * Finds a person by ID.
     */
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Creates a new person with addresses in the database.
     */
    public Person createPerson(Person person) {
        // Synchronize both sides of the relationship before saving.
        if (person.getAddresses() != null) {
            person.getAddresses().forEach(address -> address.setPerson(person));
        }
        return personRepository.save(person);
    }

    /**
     * Updates an existing person.
     */
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
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    // ==================== ADDRESS MANAGEMENT ====================

    /**
     * Adds an address to an existing person.
     */
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
    public List<Person> findByLastName(String lastName) {
        return personRepository.findByLastName(lastName);
    }

    /**
     * Finds persons by first name (partial, case-insensitive).
     */
    public List<Person> findByFirstNameContaining(String firstName) {
        return personRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    /**
     * Finds a person by email.
     */
    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    /**
     * Finds persons with an address in the given city.
     */
    public List<Person> findByCity(String city) {
        return personRepository.findByAddresses_City(city);
    }

    /**
     * Finds persons by address type.
     */
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
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Finds order by person ID
     */
    public List<Order> findOrdersByPersonId(Long personId) {
        return orderRepository.findByPersonId(personId);
    }

    /**
     * Finds the 10 most recent orders.
     */
    public List<Order> findLatestOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }
}