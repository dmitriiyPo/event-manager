package dev.sorokin.eventmanager.converter.locations;

import dev.sorokin.eventmanager.entity.LocationsEntity;
import dev.sorokin.eventmanager.model.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationsEntityConverter {

    public LocationsEntity toEntity(Locations locations) {
        return new LocationsEntity(
                locations.id(),
                locations.name(),
                locations.address(),
                locations.capacity(),
                locations.description()
        );
    }


    public Locations toDomain(LocationsEntity locations) {
        return new Locations(
                locations.getId(),
                locations.getName(),
                locations.getAddress(),
                locations.getCapacity(),
                locations.getDescription()
        );
    }

}
