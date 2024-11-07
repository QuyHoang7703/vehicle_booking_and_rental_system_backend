package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DropOffLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "bus_trip_id")
    @JsonIgnore
    private BusTrip busTrip;
}
