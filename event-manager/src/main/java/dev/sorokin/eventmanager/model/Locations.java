package dev.sorokin.eventmanager.model;

public record Locations(

        Long id,

        String name,

        String address,

        Integer capacity,

        String description
) {
}
