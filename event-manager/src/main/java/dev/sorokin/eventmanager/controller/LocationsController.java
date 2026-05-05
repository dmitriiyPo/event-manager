package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.converter.locations.LocationsDtoConverter;
import dev.sorokin.eventmanager.dto.locations.LocationsDto;
import dev.sorokin.eventmanager.filter.LocationSearchFilter;
import dev.sorokin.eventmanager.model.Locations;
import dev.sorokin.eventmanager.service.LocationsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private final LocationsService locationsService;
    private final LocationsDtoConverter locationsDtoConverter;

    @Autowired
    public LocationsController(LocationsService locationsService, LocationsDtoConverter locationsDtoConverter) {
        this.locationsService = locationsService;
        this.locationsDtoConverter = locationsDtoConverter;
    }


    @PostMapping
    public ResponseEntity<LocationsDto> createLocation(@RequestBody @Valid LocationsDto locationDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationsDtoConverter.toDto(locationsService.createLocation(locationsDtoConverter.toDomain(locationDto))));
    }


    @GetMapping("/search-locations")
    public ResponseEntity<List<LocationsDto>> searchLocations(@Valid LocationSearchFilter locationSearchFilter) {
        Page<Locations> locationsPage = locationsService.searchLocations(locationSearchFilter);
        List<LocationsDto> locations = locationsPage.getContent()
                .stream()
                .map(locationsDtoConverter::toDto)
                .toList();
        return ResponseEntity.ok(locations);
    }


    @GetMapping
    public ResponseEntity<List<LocationsDto>> getAllLocations() {
        List<Locations> locations = locationsService.getAllLocations();
        return ResponseEntity.ok(locations.stream().map(locationsDtoConverter::toDto).toList());
    }


    @GetMapping("/{locationId}")
    public ResponseEntity<LocationsDto> getLocation(@PathVariable("locationId") Long locationId) {
        return ResponseEntity.ok(locationsDtoConverter.toDto(locationsService.getLocation(locationId)));
    }


    @PutMapping("/{locationId}")
    public ResponseEntity<LocationsDto> updateLocation(
            @PathVariable("locationId") Long locationId,
            @RequestBody @Valid LocationsDto locationDto)
    {
        return ResponseEntity.ok(locationsDtoConverter.toDto(locationsService.updateLocation(locationId, locationsDtoConverter.toDomain(locationDto))));
    }


    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("locationId") Long locationId) {
        locationsService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }

}
