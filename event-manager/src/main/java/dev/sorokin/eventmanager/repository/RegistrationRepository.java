package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    Optional<RegistrationEntity> findByEventIdAndUserId(Long eventId, Long userId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    void deleteByEventId(Long eventId);
}
