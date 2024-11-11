//package com.pbl6.VehicleBookingRental.user.domain.bus_service;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
//@Entity
//@Getter
//@Setter
//public class DepartTimeBusTrip {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
//    private LocalTime departureTime;
//
//    @ManyToOne
//    @JoinColumn(name = "bus_trip_id")
//    private BusTrip busTrip;
//
//}
