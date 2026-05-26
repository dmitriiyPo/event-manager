package dev.sorokin.eventmanager.dto.events.response;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


public record EventResponseDto(

        @NotNull
        Long id,

        @Size(max = 40)
        @NotBlank
        String name,

        @NotNull
        Long ownerId,

        @NotNull
        Integer maxPlaces,

        @NotNull
        Integer occupiedPlaces,

        @NotNull
        @Future
        LocalDateTime date,

        @Min(1)
        @NotNull
        Integer cost,

        @Min(30)
        @NotNull
        Integer duration,

        @NotNull
        Long locationId,

        @NotBlank
        String status
) {
}
