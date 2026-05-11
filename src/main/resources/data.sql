-- Test users for development / Test-Benutzer für Entwicklung
-- Passwords are BCrypt hashed / Passwörter sind BCrypt-gehasht

-- admin / admin123
INSERT IGNORE INTO users (username, password, role, enabled, account_non_locked)
VALUES ('admin',
        '$2a$10$GOaTk2S3oCxOBMQvxx6DU.9rnYRVYE/zt/Q9eKzmBpleAi0iBRTnW',
        'ADMIN', true, true);

-- user / user123
INSERT IGNORE INTO users (username, password, role, enabled, account_non_locked)
VALUES ('user',
        '$2a$10$KDcXhfWTnWrocHvssMOeneDUQxPdOE7Hhr7lRljMjHsXIo5ttAPV2',
        'USER', true, true);