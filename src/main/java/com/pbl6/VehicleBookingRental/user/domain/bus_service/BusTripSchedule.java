package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class BusTripSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
//    private double priceTicket;
    private double discountPercentage;
    private int availableSeats;
    private double ratingTotal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
    private boolean isOperation;

    @ManyToOne
    @JoinColumn(name="bus_trip_id")
    private BusTrip busTrip;

    @ManyToOne
    @JoinColumn(name="bus_id")
    private Bus bus;

    @OneToMany(mappedBy = "busTripSchedule", cascade = CascadeType.ALL)
    private List<BreakDay> breakDays;

    @OneToMany(mappedBy = "busTripSchedule")
    private List<OrderBusTrip> orderBusTrips;



}
