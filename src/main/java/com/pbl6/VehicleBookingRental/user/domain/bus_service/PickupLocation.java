package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Entity
@Data
public class PickupLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String provinceName ;

    private String name;

//    @ManyToOne
//    @JoinColumn(name = "bus_trip_id")
//    @JsonIgnore
//    private BusTrip busTrip;
    @ManyToMany(mappedBy = "pickupLocations")
    @JsonIgnore
    private List<BusTrip> busTrips;

}
