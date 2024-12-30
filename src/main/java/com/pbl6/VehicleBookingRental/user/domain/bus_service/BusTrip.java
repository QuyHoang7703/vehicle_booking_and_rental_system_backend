package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
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
//    private Duration durationJourney ;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String pickupLocations;
//    @Column(columnDefinition = "MEDIUMTEXT")
//    private String dropOffLocations;

    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    @JsonIgnore
    private BusPartner busPartner;


    @OneToMany(mappedBy = "busTrip")
    @JsonIgnore
    private List<BusTripSchedule> busTripSchedules;

    @OneToMany(mappedBy = "busTrip", cascade = CascadeType.ALL)
    private List<DropOffLocation> dropOffLocations;
}
