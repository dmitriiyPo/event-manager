package dev.sorokin.eventmanager.dto.locations;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record LocationsDto(

        @Null
        Long id,

        @Size(max = 30)
        @NotBlank(message = "Name must be not empty")
        String name,

        @Size(max = 30)
        @NotBlank(message = "Address must be not empty")
        String address,

        @Min(value = 5, message = "Capacity must be greater than or equal to 5")
        @NotNull
        Integer capacity,

        @Size(max = 30)
        @Nullable
        String description
) {
}
