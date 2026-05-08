package com.saki.personmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity representing a postal address.
 *
 * <p>ManyToOne relationship to Person - many addresses belong to one person.</p>
 *
 * @author saki
 */
@Entity
@Table(name="addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street must not be blank")
    @Size(min = 3, max = 100, message = "Street must be between 3 and 100 characters")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "City must not be blank")
    @Size(min = 3, max = 50, message = "City must be between 3 and 50 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Zip code must not be blank")
    @Size(min = 3, max = 10, message = "Zip code must be between 3 and 10 characters")
    @Column(nullable = false)
    private String zipCode;

    @NotBlank(message = "Country must not be blank")
    @Size(min = 3, max = 50, message = "Country must be between 3 and 50 characters")
    @Column(nullable = false)
    private String country;

    /**
     * Type of address - stored as String in database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AddressType addressType;


    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name="person_id")
    @JsonBackReference // Breaks JSON infinite loop
    private Person person;
}
