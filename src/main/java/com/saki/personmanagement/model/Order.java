package com.saki.personmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an order in the database.
 *
 * <p>ManyToOne relationship to Person - many orders belong to one person.</p>
 *
 * @author saki
 */
@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order number must not be blank")
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, length = 100, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Order status - set automatically by @PrePersist.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Order date - set automatically by @PrePersist.
     */
    @Column(nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="person_id")
    @ToString.Exclude
    @JsonBackReference("person-orders") // Unique name to avoid conflict with addresses
    private Person person;

    // ==================== Lifecycle Callbacks ====================

    /**
     *  automatically set default values before first INSERT.
     */
    @PrePersist
    protected void onCreate() {
        if( orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if( status == null) {
            status = OrderStatus.PENDING;
        }
    }
}
