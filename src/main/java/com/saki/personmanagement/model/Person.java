package com.saki.personmanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a person in the database.
 *
 * <p>OneToMany relationships to Address and Order.</p>
 *
 * @author saki
 */
@Entity
@Table(name="persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Email(message = "Email must be valid")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    /**
     * OneToMany - one person can have multiple addresses.
     *
     * mappedBy = "person" -> Address.person is the owner
     * cascade = ALL → operations cascade to addresses
     * orphanRemoval = true → removes addresses without person
     * fetch = LAZY → addresses loaded only when accessed
     *
     */
    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude // exclude addresses from toString(), otherwise it would be recursive
    @JsonManagedReference // Jackson manages the "onwner" side of the relationship
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @ToString.Exclude // avoids infinite loop in JSON
    @JsonManagedReference(value = "person-orders") // Must match name in Order.java
    private List<Order> orders = new ArrayList<>();

    // ======================== HELPER METHODS ========================

    /**
     * adds an address and synchornizes the other side of the relationship.
     * @param address
     */
    public void addAddress(Address address) {
        addresses.add(address);
        address.setPerson(this); // synchronize the other side of the relationship
    }

    /**
     * removes an address and synchronizes the other side of the relationship.
     * @param address
     */
    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setPerson(null); // synchronize the other side of the relationship
    }

    /**
     * adds on order and synchronizes both sides of the relationship.
     * @param order
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setPerson(this);
    }

    /**
     * removes an order and synchronizes both sides of the relationship.
     * @param order
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setPerson(null);
    }

}
