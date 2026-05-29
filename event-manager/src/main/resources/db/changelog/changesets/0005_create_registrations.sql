-- liquibase formatted sql
-- changeset dmitriy:0005-create-registrations
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events (id),
    user_id BIGINT NOT NULL REFERENCES users (id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_registration_event_user UNIQUE (event_id, user_id)
);
--rollback DROP TABLE registrations;

