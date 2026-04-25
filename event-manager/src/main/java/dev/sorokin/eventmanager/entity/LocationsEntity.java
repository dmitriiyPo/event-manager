package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class LocationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "description")
    private String description;


    public LocationsEntity() {}


    public LocationsEntity(Long id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
