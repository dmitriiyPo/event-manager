package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.converter.events.EventDtoConverter;
import dev.sorokin.eventmanager.dto.events.response.EventResponseDto;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EventDtoConverter eventDtoConverter;


    @Autowired
    public RegistrationController(RegistrationService registrationService, EventDtoConverter eventDtoConverter) {
        this.registrationService = registrationService;
        this.eventDtoConverter = eventDtoConverter;
    }


    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registrationForEvent(@PathVariable("eventId") Long eventId) {
        registrationService.registrationForEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistrationForEvent(@PathVariable("eventId") Long eventId) {
        registrationService.cancelRegistrationForEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getMyEvents() {
        List<Event> events = registrationService.getMyEvents();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events.stream().map(eventDtoConverter::toDto).toList());
    }

}
