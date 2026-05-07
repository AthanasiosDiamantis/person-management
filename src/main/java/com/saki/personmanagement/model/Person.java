package com.saki.personmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity representing a person in the database.
 *
 * <p>Hibernate automatically creates the 'persons' table based on this class.</p>
 *
 * @author saki
 */
@Entity
@Table(name="persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name - mandatory, max 100 characters.
     * Vorname - Pflichtfeld, max 100 Zeichen.
     */
    @NotBlank( message = "Last name must not be blank")
    @Column(name="first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Last name - mandatory, max 100 characters.
     * Nachname - Pflichtfeld, max 100 Zeichen.
     */
    @NotBlank( message = "Last name must not be blank")
    @Column(name="last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Email - must be valid format, unique in database.
     * Email - muss gültiges Format haben, eindeutig in der Datenbank.
     */
    @Email( message = "Email must be valid format")
    @Column(name="email", unique = true, nullable = false, length = 150)
    private String email;


    // ============================== Constructors ==================================

    /**
     * Default constructor required by JPA/Hibernate.
     * Standard-Konstruktor wird von JPA/Hibernate benötigt.
     */
    public Person() {
        // default constructor
    }

    /**
     * Constructor for creating a new person.
     * Konstruktor zum Erstellen einer neuen Person.
     */
    public Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ============================== Getters and Setters ==============================

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


}
