package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class BusTripSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="bus_trip_id")
    private BusTrip busTrip;

    @ManyToOne
    @JoinColumn(name="bus_id")
    private Bus bus;

    @OneToMany(mappedBy = "busTripSchedule")
    private List<BreakDay> breakDayList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;

}
