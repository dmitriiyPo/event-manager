package dev.sorokin.eventmanager.converter.events;

import dev.sorokin.eventmanager.dto.events.request.EventRequestDto;
import dev.sorokin.eventmanager.dto.events.response.EventResponseDto;
import dev.sorokin.eventmanager.model.Event;
import org.springframework.stereotype.Component;


@Component
public class EventDtoConverter {


    public EventResponseDto toDto(Event event) {
        return new EventResponseDto(
                event.id(),
                event.name(),
                event.userId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status().name()
        );
    }


    public Event toDomain(EventRequestDto event) {
        return new Event(
                null,
                event.name(),
                null,
                event.maxPlaces(),
                null,
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                null
        );
    }

}
