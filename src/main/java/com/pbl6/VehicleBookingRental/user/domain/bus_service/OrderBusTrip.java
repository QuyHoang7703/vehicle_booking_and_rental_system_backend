package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "OrderBusTrip")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderBusTrip {
    @Id
    @Column(name = "order_id")
    private String id;

    private int numberOfTicket;

    private double pricePerTicket;

    private double priceTotal;

    private OrderStatusEnum status;

    private String departureLocation;

    private String arrivalLocation;

    private double discountPercentage;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;
    private LocalDate departureDate;
    private Duration journeyDuration;

//    private Instant departureDateTime;
    private Instant arrivalTime;

    private double voucherDiscount;

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