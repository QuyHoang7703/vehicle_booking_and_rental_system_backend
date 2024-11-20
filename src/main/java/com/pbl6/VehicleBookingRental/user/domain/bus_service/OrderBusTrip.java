package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "OrderBusTrip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderBusTrip {
    @Id
    @Column(name = "order_id")
    private String id;

    private int numberOfTicket;

    private double priceTotal;

//    private String status;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDate departureDate;

//    private Instant departureDateTime;
//    private Instant arrivalDateTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @ManyToOne
    @JoinColumn(name = "bus_trip_schedule_id")
    @JsonIgnore
    private BusTripSchedule busTripSchedule;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;


}