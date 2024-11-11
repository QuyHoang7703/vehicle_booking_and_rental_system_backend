package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String durationJourney ;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String pickupLocation;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String dropOffLocation;

    @ManyToOne
    @JoinColumn(name = "bus_partner_id")
    @JsonIgnore
    private BusPartner busPartner;

    @ManyToMany
    @JoinTable(
            name="bus_trip_pickup_location",
            joinColumns = @JoinColumn(name="bus_trip_id"),
            inverseJoinColumns = @JoinColumn(name="pickup_location_id"))
    private List<PickupLocation> pickupLocations;

    @ManyToMany
    @JoinTable(
            name="bus_trip_drop_off_location",
            joinColumns = @JoinColumn(name="bus_trip_id"),
            inverseJoinColumns = @JoinColumn(name="drop_off_location_id")
    )
    private List<DropOffLocation> dropOffLocations;

    @OneToMany(mappedBy = "busTrip")
    @JsonIgnore
    private List<BusTripSchedule> busTripSchedules;

}
