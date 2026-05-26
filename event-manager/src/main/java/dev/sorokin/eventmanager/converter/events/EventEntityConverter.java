package dev.sorokin.eventmanager.converter.events;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.model.Event;
import org.springframework.stereotype.Component;


@Component
public class EventEntityConverter {


    public EventEntity toEntity(Event event) {
        return new EventEntity(
                null,
                event.name(),
                null,
                event.maxPlaces(),
                null,
                event.date(),
                event.cost(),
                event.duration(),
                null,
                null
        );
    }


    public Event toDomain(EventEntity event) {
        return new Event(
                event.getId(),
                event.getName(),
                event.getUser().getId(),
                event.getMaxPlaces(),
                event.getOccupiedPlaces(),
                event.getDate(),
                event.getCost(),
                event.getDuration(),
                event.getLocations().getId(),
                EventStatus.valueOf(event.getStatus())
        );
    }

}
