package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.converter.locations.LocationsEntityConverter;
import dev.sorokin.eventmanager.entity.LocationsEntity;
import dev.sorokin.eventmanager.filter.LocationSearchFilter;
import dev.sorokin.eventmanager.model.Locations;
import dev.sorokin.eventmanager.repository.LocationsRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class LocationsService {

    private final LocationsRepository locationsRepository;
    private final LocationsEntityConverter locationsEntityConverter;

    @Autowired
    public LocationsService(LocationsRepository locationsRepository, LocationsEntityConverter locationsEntityConverter) {
        this.locationsRepository = locationsRepository;
        this.locationsEntityConverter = locationsEntityConverter;
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {EntityExistsException.class}
    )
    public Locations createLocation(Locations location) {
        if (locationsRepository.existsByName(location.name())) {
            throw new EntityExistsException("Location with name=%s already exist".formatted(location.name()));
        }
        LocationsEntity saveToLocation = locationsEntityConverter.toEntity(location);
        LocationsEntity savedLocation = locationsRepository.save(saveToLocation);
        return locationsEntityConverter.toDomain(savedLocation);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ
    )
    public Page<Locations> searchLocations(LocationSearchFilter locationSearchFilter) {

        int pageSize = locationSearchFilter.pageSize() != null ? locationSearchFilter.pageSize() : 3;
        int pageNumber = locationSearchFilter.pageNumber() != null ? locationSearchFilter.pageNumber() : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<LocationsEntity> page = locationsRepository.searchLocations(
                locationSearchFilter.name(),
                locationSearchFilter.address(),
                locationSearchFilter.capacity(),
                pageable
        );

        return page.map(locationsEntityConverter::toDomain);
    }


    @Transactional(readOnly = true)
    public List<Locations> getAllLocations() {
        List<LocationsEntity> locations = locationsRepository.findAll();
        return locations.stream().map(locationsEntityConverter::toDomain).toList();
    }


    @Transactional(readOnly = true)
    public Locations getLocation(Long locationId) {
        LocationsEntity foundLocation = locationsRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location with id=%s not found".formatted(locationId)));
        return locationsEntityConverter.toDomain(foundLocation);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {EntityNotFoundException.class}
    )
    public Locations updateLocation(Long locationId, Locations location) {
        if (!locationsRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location with id=%s not found".formatted(locationId));
        }
        if (locationsRepository.existsByName(location.name())) {
            throw new EntityExistsException("Location with name=%s already exist".formatted(location.name()));
        }
        LocationsEntity updatedLocation = locationsEntityConverter.toEntity(location);
        updatedLocation.setId(locationId);
        LocationsEntity savedLocation = locationsRepository.save(updatedLocation);
        return locationsEntityConverter.toDomain(savedLocation);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {EntityNotFoundException.class}
    )
    public void deleteLocation(Long locationId) {
        if (!locationsRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location with id=%s not found".formatted(locationId));
        }
        locationsRepository.deleteById(locationId);
    }

}
