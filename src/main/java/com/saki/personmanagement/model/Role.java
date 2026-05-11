package com.saki.personmanagement.model;

/**
 * Enum representing user roles for authorization.
 * Enum der Benutzerrollen für die Autorisierung repräsentiert.
 *
 * @author saki
 */
public enum Role {
    USER,       // Standard user
    ADMIN,      // Administrator with full access
    MODERATOR   // Moderator with limited admin rights
}
