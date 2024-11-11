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
    private String arrivalLocation ;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startOperationDay;
//    private Instant departureTime ;
    private String durationJourney ;
    private double priceTicket;
    private boolean status;
    private double discountPercentage;
    private int availableSeats ;
    private double ratingTotal;
    private Instant updateAt ;

    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    private BusPartner busPartner;

    @OneToMany(mappedBy = "busTrip")
    private List<PickupLocation> pickupLocationList;

    @OneToMany(mappedBy = "busTrip")
    private List<DropOffLocation> dropOffLocationList;

    @OneToMany(mappedBy = "busTrip")
    private List<BusTripSchedule> busTripScheduleList;

}
