package dev.sorokin.eventmanager.model;

import dev.sorokin.eventmanager.enums.EventStatus;

import java.time.LocalDateTime;

public record Event (

        Long id,

        String name,

        Long userId,

        Integer maxPlaces,

        Integer occupiedPlaces,

        LocalDateTime date,

        Integer cost,

        Integer duration,

        Long locationId,

        EventStatus status
) {
}
