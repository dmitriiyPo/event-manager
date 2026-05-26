package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = @UniqueConstraint(
        name = "uk_registration_event_user",
        columnNames = {"event_id", "user_id"}
        ))
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public RegistrationEntity() {}

    public RegistrationEntity(Long id, EventEntity event, UserEntity user, LocalDateTime createdAt) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.createdAt = createdAt;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
