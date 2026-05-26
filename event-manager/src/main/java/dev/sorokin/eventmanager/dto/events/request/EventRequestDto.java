package dev.sorokin.eventmanager.dto.events.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


public record EventRequestDto(

        @Size(max = 40)
        @NotBlank
        String name,

        @Min(0)
        @NotNull
        Integer maxPlaces,

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
        Long locationId
) {
}
