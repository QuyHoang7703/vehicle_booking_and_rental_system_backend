package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bus_trip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String  departureLocation ;
    private Instant departureTime ;
    private String destination ;
    private double durationJourney ;
    private double priceTicket;
//    private String status ;
    private Instant updateAt ;
    private double discountPercentage;
    private int availableSeats ;
    private double ratingTotal;

    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    private BusPartner busPartner;

    @ManyToOne
    @JoinColumn(name= "bus_id")
    private Bus bus ;

    @OneToMany(mappedBy = "busTrip")
    private List<BreakDay> breakDayList;

    @OneToMany(mappedBy = "busTrip")
    private List<PickupLocation> pickupLocationList;

    @OneToMany(mappedBy = "busTrip")
    private List<DropOffLocation> dropOffLocationList;

}
