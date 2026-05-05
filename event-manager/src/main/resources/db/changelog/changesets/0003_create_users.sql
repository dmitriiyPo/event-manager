-- liquibase formatted sql
-- changeset dmitriy:0003-create-users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    age INT NOT NULL CHECK (age >= 18),
    role VARCHAR(255) NOT NULL
);
--rollback DROP TABLE users;