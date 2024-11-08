package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
//    private Instant departureTime ;
    private String arrivalLocation ;
    private String durationJourney ;
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

    @OneToMany(mappedBy = "busTrip", cascade = CascadeType.ALL)
    private List<BreakDay> breakDayList;

    @OneToMany(mappedBy = "busTrip", cascade = CascadeType.ALL)
    private List<PickupLocation> pickupLocationList;

    @OneToMany(mappedBy = "busTrip", cascade = CascadeType.ALL)
    private List<DropOffLocation> dropOffLocationList;

    @OneToMany(mappedBy = "busTrip", cascade = CascadeType.ALL)
    private List<DepartTimeBusTrip> departTimeBusTripList;

}
