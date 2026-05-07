package com.saki.personmanagement.service;

import com.saki.personmanagement.model.Person;
import com.saki.personmanagement.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for person management business logic.
 *
 * <p>Coordinates between controller and repository layer.</p>
 *
 * @author saki
 */
@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    /**
     * Constructor injection - best practice in Spring!
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Returns all persons from the database.
     */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Finds a person by ID.
     *
     * @param id the person ID
     * @return the person wrapped in Optional
     */
    public Optional<Person> getPersonById(Long id ) {
        return personRepository.findById(id);
    }

    /**
     * Creates a new person in the database.
     *
     * @param person the person to create
     * @return the created person with generated ID
     */

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    /**
     * Updates an existing person.
     *
     * @param id the ID of the person to update
     * @param personDetails the new person data
     * @return the updated person
     * @throws RuntimeException if person not found
     */
    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        person.setFirstName(personDetails.getFirstName());
        person.setLastName(personDetails.getLastName());
        person.setEmail(personDetails.getEmail());
        return personRepository.save(person);
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the ID of the person to delete
     */
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

}
