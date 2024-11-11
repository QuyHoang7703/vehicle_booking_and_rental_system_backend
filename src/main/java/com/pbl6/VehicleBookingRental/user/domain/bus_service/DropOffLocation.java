package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class DropOffLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String provinceName ;

    private String name;

    @ManyToMany(mappedBy = "dropOffLocations")
    @JsonIgnore
    private List<BusTrip> busTrip;
}
