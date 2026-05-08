package com.saki.personmanagement.model;

/**
 * Enum representing the status of an order.
 *
 * <p>Always use EnumType.STRING in @Enumerated - never ORDINAL!</p>
 *
 * @author saki
 */
public enum OrderStatus {
    PENDING,    // Order has been placed, but not yet confirmed
    CONFIRMED,  // Order has been confirmed and is ready for shipping
    SHIPPED,    // Order has been shipped, but not yet delivered
    DELIVERED,  // Order has been delivered
    CANCELLED   // Order has been canceled
}
