package com.saki.personmanagement.controller;

import com.saki.personmanagement.model.Person;
import com.saki.personmanagement.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for person management endpoints.
 *
 * <p>Handles HTTP requests and delegates to PersonService.</p>
 *
 * @author saki
 */
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    /**
     * Constructor injection of PersonService.
     */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * GET /api/persons - Returns all persons.
     */
    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    /**
     * GET /api/persons/{id} - Returns a person by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    /**
     * POST /api/persons - Creates a new person.
     *
     * @Valid triggers Bean Validation before processing.
     */
    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {
        Person createdPerson = personService.createPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
    }

    /**
     * PUT /api/persons/{id} - Updates an existing person.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody Person personDetails) {
        try {
            Person updatePerson = personService.updatePerson(id, personDetails);
            return ResponseEntity.status(HttpStatus.OK).body(updatePerson);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/persons/{id} - Deletes a person by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

}
