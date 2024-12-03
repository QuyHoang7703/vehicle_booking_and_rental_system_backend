package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropOffLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String province;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String dropOffLocation;
    private double priceTicket;
    private Duration journeyDuration;
    private Instant createdAt;
    private Instant updateAt;

    @ManyToOne
    @JoinColumn(name = "bus_trip_id")
    private BusTrip busTrip;

    @PrePersist
    public void prePersist(){
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updateAt = Instant.now();
    }
}

