package com.saki.personmanagement.controller;

import com.saki.personmanagement.model.Address;
import com.saki.personmanagement.model.AddressType;
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

    // ==================== PERSON ENDPOINTS ====================

    /**
     * GET /api/persons - Returns all persons.
     */
    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    /**
     * GET /api/persons/with-addresses - Returns all persons with addresses (solves N+1).
     */
    @GetMapping("/with-addresses")
    public List<Person> getAllPersonsWithAddresses() {
        return personService.getAllPersonsWithAddresses();
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
        Person created = personService.createPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * POST /api/persons/{id}/addresses - Adds an address to an existing person.
     * Fügt eine Adresse zu einer bestehenden Person hinzu.
     */
    @PostMapping("/{id}/addresses")
    public ResponseEntity<Person> addAddress(
            @PathVariable Long id,
            @Valid @RequestBody Address address) {
        return personService.addAddress(id, address)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // ==================== SEARCH ENDPOINTS ====================

    /**
     * GET /api/persons/search/by-lastname?lastName=... - Search by last name.
     * Suche nach Nachname.
     */
    @GetMapping("/search/by-lastname")
    public List<Person> searchByLastName(@RequestParam String lastName) {
        return personService.findByLastName(lastName);
    }

    /**
     * GET /api/persons/search/by-firstname?firstName=... - Search by first name (partial).
     * Suche nach Vorname (Teilsuche).
     */
    @GetMapping("/search/by-firstname")
    public List<Person> searchByFirstName(@RequestParam String firstName) {
        return personService.findByFirstNameContaining(firstName);
    }

    /**
     * GET /api/persons/search/by-email?email=... - Search by email.
     * Suche nach Email.
     */
    @GetMapping("/search/by-email")
    public ResponseEntity<Person> searchByEmail(@RequestParam String email) {
        return personService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/persons/search/by-city?city=... - Search persons by city.
     * Suche Personen nach Stadt.
     */
    @GetMapping("/search/by-city")
    public List<Person> searchByCity(@RequestParam String city) {
        return personService.findByCity(city);
    }

    /**
     * GET /api/persons/search/by-address-type?addressType=... - Search by address type.
     * Suche nach Adresstyp.
     */
    @GetMapping("/search/by-address-type")
    public List<Person> searchByAddressType(@RequestParam AddressType addressType) {
        return personService.findByAddressType(addressType);
    }

}
