package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.LocationsEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationsRepository extends JpaRepository<LocationsEntity, Long> {

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT l FROM LocationsEntity l
        WHERE (:name IS NULL OR l.name = :name)
        AND (:address IS NULL OR l.address = :address)
        AND (:capacity IS NULL OR l.capacity < :capacity)
    """)
    Page<LocationsEntity> searchLocations(
            @Param("name") String name,
            @Param("address") String address,
            @Param("capacity") Integer capacity,
            Pageable pageable
    );
}
