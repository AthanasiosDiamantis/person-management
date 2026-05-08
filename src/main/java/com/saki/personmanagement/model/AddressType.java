package com.saki.personmanagement.model;

/**
 * Enum representing the type of an address.
 *
 * <p>Always use EnumType.STRING in @Enumerated - never ORDINAL!</p>
 *
 * @author saki
 */
public enum AddressType {
    HOME,  // Primary residence
    WORK,  // Business address
    BILLING, // Billing address
    SHIPPING // Shipping address

}
