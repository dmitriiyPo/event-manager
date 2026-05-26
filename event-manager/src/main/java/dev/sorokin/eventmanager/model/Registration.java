package dev.sorokin.eventmanager.model;

import java.time.LocalDateTime;

public record Registration(

        Long id,

        Long eventId,

        Long userId,

        LocalDateTime createdAt
) {
}
