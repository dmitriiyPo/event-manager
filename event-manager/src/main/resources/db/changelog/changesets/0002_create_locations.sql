-- liquibase formatted sql
-- changeset dmitriy:0002-create-locations
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    capacity INT NOT NULL CHECK (capacity >= 5),
    description VARCHAR(255)
);
--rollback DROP TABLE locations;