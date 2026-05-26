package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.converter.events.EventEntityConverter;
import dev.sorokin.eventmanager.converter.user.UsersEntityConverter;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationsEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.filter.EventSearchFilter;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationsRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.security.jwt.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntityConverter eventEntityConverter;
    private final AuthenticationService authenticationService;
    private final UsersEntityConverter usersEntityConverter;
    private final EventPermissionService eventPermissionService;
    private final LocationsRepository locationsRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;


    @Autowired
    public EventService(
            EventRepository eventRepository,
            EventEntityConverter eventEntityConverter,
            AuthenticationService authenticationService,
            UsersEntityConverter usersEntityConverter,
            EventPermissionService eventPermissionService,
            LocationsRepository locationsRepository,
            RegistrationRepository registrationRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventEntityConverter = eventEntityConverter;
        this.authenticationService = authenticationService;
        this.usersEntityConverter = usersEntityConverter;
        this.eventPermissionService = eventPermissionService;
        this.locationsRepository = locationsRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {IllegalArgumentException.class, EntityNotFoundException.class}
    )
    public Event createEvent(Event event) {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();
        UserEntity userEntity = usersEntityConverter.toEntity(user);

        LocationsEntity locationEntity = locationsRepository.findById(event.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id=%s not found".formatted(event.locationId())));

        if (locationEntity.getAvailableCapacity() < event.maxPlaces()) {
            throw new IllegalArgumentException("Not enough capacity places in this location for this event. Available seats = %s".formatted(locationEntity.getAvailableCapacity()));
        }

        if (event.date().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date cannot be before current date.");
        }

//        if (event.date().isBefore(LocalDateTime.now().plusHours(1))) {
//            throw new IllegalArgumentException("Event must start at least 1 hour from now.");
//        }

        EventEntity createToEvent = eventEntityConverter.toEntity(event);
        createToEvent.setUser(userEntity);
        createToEvent.setLocations(locationEntity);
        createToEvent.setOccupiedPlaces(0);
        createToEvent.setStatus(EventStatus.WAIT_START.name());
        EventEntity createdEvent = eventRepository.save(createToEvent);
        return eventEntityConverter.toDomain(createdEvent);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {IllegalArgumentException.class, EntityNotFoundException.class}
    )
    public void deleteEvent(Long eventId) {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();
        Event currentEvent = getEventById(eventId);

        if (!eventPermissionService.canModify(user, currentEvent)) {
            throw new IllegalArgumentException("Not enough permissions to modify this event.");
        }

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));

        if (!(EventStatus.WAIT_START.name().equals(eventEntity.getStatus()))) {
            throw new IllegalArgumentException("This event has already started, it cannot be canceled.");
        }

        registrationRepository.deleteByEventId(eventId);
        eventEntity.setStatus(EventStatus.CANCELLED.name());
        eventRepository.save(eventEntity);
    }


    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        EventEntity foundEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));
        return eventEntityConverter.toDomain(foundEvent);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {IllegalArgumentException.class, EntityNotFoundException.class}
    )
    public Event updateEvent(Long eventId, Event eventRequest) {

        User currentAuthUser = authenticationService.getCurrentAuthenticatedUserOrThrow();

        Event currentEvent = getEventById(eventId);

        if (!eventPermissionService.canModify(currentAuthUser, currentEvent)) {
            throw new IllegalArgumentException("Not enough permissions to modify this eventRequest.");
        }

        if (eventRequest.maxPlaces() <= currentEvent.occupiedPlaces()) {
            throw new IllegalArgumentException("Not enough capacity places in this eventRequest.");
        }

        if (eventRequest.date().isBefore(currentEvent.date())) {
            throw new IllegalArgumentException("New date cannot be before current eventRequest date.");
        }

        if (eventRequest.date().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past.");
        }

        if (eventRequest.date().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("Event must start at least 1 hour from now.");
        }

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));

        if ((EventStatus.CANCELLED.name().equals(eventEntity.getStatus()))) {
            throw new IllegalArgumentException("This eventRequest has been cancelled and cannot be changed.");
        }

        LocationsEntity locationEntity = locationsRepository.findById(eventRequest.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id=%s not found".formatted(eventRequest.locationId())));

        UserEntity userEntity = userRepository.findById(currentEvent.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with id=%s not found".formatted(currentEvent.userId())));

        EventEntity eventToUpdate = eventEntityConverter.toEntity(eventRequest);
        eventToUpdate.setId(eventId);
        eventToUpdate.setUser(userEntity);
        eventToUpdate.setOccupiedPlaces(currentEvent.occupiedPlaces());
        eventToUpdate.setLocations(locationEntity);
        eventToUpdate.setStatus(currentEvent.status().name());

        EventEntity updatedEvent = eventRepository.save(eventToUpdate);
        return eventEntityConverter.toDomain(updatedEvent);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ
    )
    public Page<Event> searchEvents(EventSearchFilter filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 3;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<EventEntity> page = eventRepository.searchEvent(
                filter.name(),
                filter.placesMin(),
                filter.placesMax(),
                filter.dateStartAfter(),
                filter.dateStartBefore(),
                filter.costMin(),
                filter.costMax(),
                filter.durationMin(),
                filter.durationMax(),
                filter.locationId(),
                filter.eventStatus(),
                pageable
        );

        return page.map(eventEntityConverter::toDomain);
    }


    @Transactional(readOnly = true)
    public List<Event> getMyEvents() {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();
        List<EventEntity> events = eventRepository.findByUserId(user.id());
        return events.stream().map(eventEntityConverter::toDomain).toList();
    }

}
