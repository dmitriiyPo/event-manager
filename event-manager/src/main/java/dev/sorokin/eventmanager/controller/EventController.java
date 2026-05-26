package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.converter.events.EventDtoConverter;
import dev.sorokin.eventmanager.dto.events.request.EventRequestDto;
import dev.sorokin.eventmanager.dto.events.response.EventResponseDto;
import dev.sorokin.eventmanager.filter.EventSearchFilter;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventDtoConverter eventDtoConverter;


    @Autowired
    public EventController(EventService eventService, EventDtoConverter eventDtoConverter) {
        this.eventService = eventService;
        this.eventDtoConverter = eventDtoConverter;
    }


    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody @Valid EventRequestDto eventRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventDtoConverter
                        .toDto(eventService.createEvent(eventDtoConverter.toDomain(eventRequestDto)))
                );
    }


    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable("eventId") Long eventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoConverter.toDto(eventService.getEventById(eventId)));
    }


    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventRequestDto eventRequestDto)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoConverter
                        .toDto(eventService.updateEvent(eventId, eventDtoConverter.toDomain(eventRequestDto)))
                );
    }


    @PostMapping("/search")
    public ResponseEntity<List<EventResponseDto>> searchEvents(@Valid EventSearchFilter eventSearchFilter) {
        Page<Event> eventsPage = eventService.searchEvents(eventSearchFilter);
        List<EventResponseDto> events = eventsPage.getContent()
                .stream()
                .map(eventDtoConverter::toDto)
                .toList();
        return ResponseEntity.ok(events);
    }


    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getMyEvents() {
        List<Event> events = eventService.getMyEvents();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events.stream().map(eventDtoConverter::toDto).toList());
    }

}
