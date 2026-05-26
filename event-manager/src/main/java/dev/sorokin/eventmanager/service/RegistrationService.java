package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.converter.events.EventEntityConverter;
import dev.sorokin.eventmanager.converter.user.UsersEntityConverter;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import dev.sorokin.eventmanager.security.jwt.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final AuthenticationService authenticationService;
    private final RegistrationRepository registrationRepository;
    private final EventEntityConverter eventEntityConverter;
    private final UsersEntityConverter usersEntityConverter;
    private final EventRepository eventRepository;


    @Autowired
    public RegistrationService(AuthenticationService authenticationService, RegistrationRepository registrationRepository, EventEntityConverter eventEntityConverter, UsersEntityConverter usersEntityConverter, EventRepository eventRepository) {
        this.authenticationService = authenticationService;
        this.registrationRepository = registrationRepository;
        this.eventEntityConverter = eventEntityConverter;
        this.usersEntityConverter = usersEntityConverter;
        this.eventRepository = eventRepository;
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {IllegalArgumentException.class, EntityNotFoundException.class}
    )
    public void registrationForEvent(Long eventId) {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.id())) {
            throw new IllegalArgumentException("User is already registered for this event");
        }

        EventEntity event = eventRepository.findByIdWithPessimisticLock(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));

        if (!EventStatus.WAIT_START.name().equals(event.getStatus())) {
            throw new IllegalArgumentException("Event with id=%s is not waiting start".formatted(eventId));
        }

        if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new IllegalArgumentException("Registration is not possible. There are no seats.");
        }

        if (user.id().equals(event.getUser().getId())) {
            throw new IllegalArgumentException("The user cannot register because he is the creator of the event.");
        }

        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);

        RegistrationEntity registration = new RegistrationEntity();
        registration.setEvent(event);
        registration.setUser(usersEntityConverter.toEntity(user));
        registration.setCreatedAt(LocalDateTime.now());

        registrationRepository.save(registration);
        eventRepository.save(event);
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {IllegalArgumentException.class, EntityNotFoundException.class}
    )
    public void cancelRegistrationForEvent(Long eventId) {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));

        RegistrationEntity foundRegistration = registrationRepository.findByEventIdAndUserId(eventId, user.id())
                .orElseThrow(() -> new EntityNotFoundException("Registration user (id=%s) on event id=%s not found".formatted(user.id(),eventId)));

        if ((EventStatus.STARTED.name().equals(event.getStatus())) || (EventStatus.FINISHED.name().equals(event.getStatus()))) {
            throw new IllegalArgumentException("Event with id=%s is not waiting start".formatted(eventId));
        }

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        registrationRepository.deleteById(foundRegistration.getId());
        eventRepository.save(event);
    }


    @Transactional(readOnly = true)
    public List<Event> getMyEvents() {
        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();
        List<EventEntity> events = eventRepository.findEventsByRegisteredUserId(user.id());
        return events.stream().map(eventEntityConverter::toDomain).toList();
    }

}
