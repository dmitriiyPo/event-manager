package dev.sorokin.eventmanager.filter;


import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record EventSearchFilter(

        String name,

        Integer placesMin,

        Integer placesMax,

        LocalDateTime dateStartAfter,

        LocalDateTime dateStartBefore,

        Integer costMin,

        Integer costMax,

        Integer durationMin,

        Integer durationMax,

        Long locationId,

        String eventStatus,

        @Min(0)
        Integer pageNumber,

        @Min(3)
        Integer pageSize
) {
}
