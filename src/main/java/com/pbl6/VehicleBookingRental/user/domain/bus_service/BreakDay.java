package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
public class BreakDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date startDay;

    private Date endDay;

    @ManyToOne
    @JoinColumn(name = "bus_trip_id")
    private BusTrip busTrip;
}
