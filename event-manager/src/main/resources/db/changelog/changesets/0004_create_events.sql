-- liquibase formatted sql
-- changeset dmitriy:0004-create-events
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users (id),
    max_places INTEGER NOT NULL CHECK (max_places > 0),
    occupied_places INTEGER NOT NULL DEFAULT 0 CHECK (occupied_places >= 0),
    date TIMESTAMP NOT NULL,
    cost INTEGER NOT NULL CHECK (cost >= 1),
    duration INTEGER NOT NULL CHECK (duration >= 30),
    location_id BIGINT NOT NULL REFERENCES locations (id),
    status VARCHAR(50) NOT NULL
);
--rollback DROP TABLE events;