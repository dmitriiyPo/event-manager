package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByUserId(Long userId);

    List<EventEntity> findByLocationsId(Long locationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventEntity e WHERE e.id = :eventId")
    Optional<EventEntity> findByIdWithPessimisticLock(@Param("eventId") Long eventId);

    @Query("""
    SELECT e FROM EventEntity e
    WHERE e.status = :status
    AND TO_CHAR(e.date, 'YYYY-MM-DD HH24:MI') = TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MI')
    """)
    List<EventEntity> findStartedEventsWithStatus(@Param("status") String status);

    @Query(value = """
    SELECT * FROM events e 
    WHERE e.status = :status 
    AND e.date + (e.duration * INTERVAL '1 minute') 
    BETWEEN CURRENT_TIMESTAMP - INTERVAL '1 minute' 
    AND CURRENT_TIMESTAMP + INTERVAL '1 minute'
    """, nativeQuery = true)
    List<EventEntity> findFinishedEventsWithStatus(String status);

    @Modifying
    @Query("""
    UPDATE EventEntity e
    SET e.status = :status
    WHERE e.id = :eventId
    """)
    void changeStatus(@Param("eventId") Long eventId, @Param("status") String status);


    @Query("""
    SELECT DISTINCT e
    FROM EventEntity e
    JOIN e.registrations r
    WHERE r.user.id = :userId
    """)
    List<EventEntity> findEventsByRegisteredUserId(@Param("userId") Long userId);


    @Query("""
        SELECT e FROM EventEntity e
        WHERE (:name IS NULL OR e.name = :name)
        AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
        AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
        AND e.date >= COALESCE(:dateStartAfter, e.date)
        AND e.date <= COALESCE(:dateStartBefore, e.date)
        AND (:costMin IS NULL OR e.cost >= :costMin)
        AND (:costMax IS NULL OR e.cost <= :costMax)
        AND (:durationMin IS NULL OR e.duration >= :durationMin)
        AND (:durationMax IS NULL OR e.duration <= :durationMax)
        AND (:locationId IS NULL OR e.locations.id = :locationId)
        AND (:eventStatus IS NULL OR e.status = :eventStatus)
    """)
    Page<EventEntity> searchEvent(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") String eventStatus,
            Pageable pageable
    );

}
