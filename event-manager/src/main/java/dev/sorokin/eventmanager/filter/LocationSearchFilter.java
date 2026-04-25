package dev.sorokin.eventmanager.filter;

import jakarta.validation.constraints.*;

public record LocationSearchFilter(

        @Size(max = 30)
        @NotBlank(message = "Name must be not empty")
        String name,

        @Size(max = 30)
        @NotBlank(message = "Address must be not empty")
        String address,

        @Min(5)
        @Max(500)
        @NotNull
        Integer capacity,

        @Min(0)
        Integer pageNumber,

        @Min(3)
        Integer pageSize
) {
}
