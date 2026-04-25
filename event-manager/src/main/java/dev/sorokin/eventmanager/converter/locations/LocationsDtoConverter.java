package dev.sorokin.eventmanager.converter.locations;

import dev.sorokin.eventmanager.dto.LocationsDto;
import dev.sorokin.eventmanager.model.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationsDtoConverter {

    public LocationsDto toDto(Locations locations) {
        return new LocationsDto(
                locations.id(),
                locations.name(),
                locations.address(),
                locations.capacity(),
                locations.description()
        );
    }


    public Locations toDomain(LocationsDto locations) {
        return new Locations(
                null,
                locations.name(),
                locations.address(),
                locations.capacity(),
                locations.description()
        );
    }

}
